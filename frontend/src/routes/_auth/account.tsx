import { useEffect, useMemo, useState } from "react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute, useRouter } from "@tanstack/react-router";
import { Button, Form, Input, Label, TextField } from "@heroui/react";

import type {
  CustomerResponse,
  EmployeeResponse,
  OwnerResponse,
} from "#/types/api";
import { useAuth } from "#/auth";
import { AccountType } from "#/types/api";
import { deleteRequest, getRequest, putRequest } from "#/utils/httpClient";
import { successToast } from "#/utils/helpers";
import Title from "#/components/Title";

const queryClient = new QueryClient();

type AccountInfo = {
  id: number;
  email: string;
  address?: string;
  numOfLoyaltyPoints?: number;
};

export const Route = createFileRoute("/_auth/account")({
  head: () => ({
    meta: [
      {
        title: "My Account | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Account />
    </QueryClientProvider>
  ),
});

type AccountDetails =
  | { kind: AccountType.CUSTOMER; data: CustomerResponse }
  | { kind: AccountType.EMPLOYEE; data: EmployeeResponse }
  | { kind: AccountType.OWNER; data: AccountInfo };

function extractErrors(error: unknown): string[] {
  if (error instanceof AggregateError) {
    return error.errors.map((entry) => String(entry));
  }

  if (error instanceof Error) {
    return [error.message];
  }

  return ["Something went wrong. Please try again."];
}

function Account() {
  const auth = useAuth();
  const router = useRouter();
  const navigate = Route.useNavigate();
  const user = auth.user;
  if (user === null) return "Error: Invalid user.";

  const [address, setAddress] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [passwordErrors, setPasswordErrors] = useState<string[]>([]);
  const [deleteErrors, setDeleteErrors] = useState<string[]>([]);
  const [passwordSuccess, setPasswordSuccess] = useState<string | null>(null);
  const [confirmingDelete, setConfirmingDelete] = useState(false);

  const isOwner = user.accountType === AccountType.OWNER;
  const isCustomer = user.accountType === AccountType.CUSTOMER;
  const isEmployee = user.accountType === AccountType.EMPLOYEE;

  const accountQuery = useQuery({
    queryKey: ["myAccount", user.id, user.accountType],
    queryFn: async (): Promise<AccountDetails> => {
      if (isCustomer) {
        const customer = await getRequest<CustomerResponse>(
          `/account/customer/${user.id}`,
        );
        return { kind: AccountType.CUSTOMER, data: customer };
      }

      if (isEmployee) {
        const employee = await getRequest<EmployeeResponse>(
          `/account/employee/${user.id}`,
        );
        return { kind: AccountType.EMPLOYEE, data: employee };
      }

      return {
        kind: AccountType.OWNER,
        data: {
          id: user.id,
          email: user.email,
        },
      };
    },
  });

  const accountInfo = useMemo(() => {
    if (accountQuery.data) {
      return accountQuery.data.data;
    }

    return {
      id: user.id,
      email: user.email,
    };
  }, [accountQuery.data, user]);

  const currentAddress =
    "address" in accountInfo ? (accountInfo.address ?? "") : "";

  const currentLoyaltyPoints =
    "numOfLoyaltyPoints" in accountInfo
      ? (accountInfo.numOfLoyaltyPoints ?? 0)
      : 0;

  useEffect(() => {
    if ("address" in accountInfo) {
      setAddress("");
    }
  }, [accountInfo]);

  const updatePasswordMutation = useMutation({
    mutationFn: async () => {
      if (password.length < 8 || password.length > 32) {
        throw new Error("Password must be between 8 and 32 characters.");
      }

      if (password !== confirmPassword) {
        throw new Error("Passwords do not match.");
      }

      if (isOwner) {
        const requestBody = {
          email: accountInfo.email,
          password,
        };

        return putRequest<OwnerResponse>(
          `/account/owner/${user.id}`,
          requestBody,
          false,
        );
      }

      if (isCustomer) {
        const requestBody = {
          email: accountInfo.email,
          password,
          address: currentAddress,
          numOfLoyaltyPoints: currentLoyaltyPoints,
        };

        return putRequest<CustomerResponse>(
          `/account/customer/${user.id}`,
          requestBody,
        );
      }

      if (user.accountType === AccountType.EMPLOYEE) {
        const requestBody = {
          email: accountInfo.email,
          password,
          address: currentAddress,
          numOfLoyaltyPoints: currentLoyaltyPoints,
        };

        return putRequest<EmployeeResponse>(
          `/account/employee/${user.id}`,
          requestBody,
        );
      }
    },
  });

  const updateAddressMutation = useMutation({
    mutationFn: async () => {
      const requestBody = {
        email: accountInfo.email,
        address,
        numOfLoyaltyPoints: currentLoyaltyPoints,
      };

      return putRequest<CustomerResponse>(
        `/account/customer/${user.id}`,
        requestBody,
      );
    },
  });

  const deleteAccountMutation = useMutation({
    mutationFn: async () => {
      return deleteRequest<void>(`/account/${user.id}`);
    },
  });

  const handleUpdatePassword = async () => {
    setPasswordErrors([]);
    setPasswordSuccess(null);

    try {
      await updatePasswordMutation.mutateAsync();
      setPassword("");
      setConfirmPassword("");
      setPasswordSuccess("Password updated successfully.");
      successToast("Password updated successfully.");
      await accountQuery.refetch();
    } catch (error) {
      setPasswordErrors(extractErrors(error));
    }
  };

  const handleAddressUpdate = async () => {
    try {
      await updateAddressMutation.mutateAsync();
      successToast("Address updated successfully.");
      await accountQuery.refetch();
    } catch (error) {}
  };

  const handleDeleteAccount = async () => {
    setDeleteErrors([]);

    try {
      await deleteAccountMutation.mutateAsync();
      await auth.logout();
      await router.invalidate();
      await navigate({ to: "/login" });
    } catch (error) {
      setDeleteErrors(extractErrors(error));
      setConfirmingDelete(false);
    }
  };

  if (accountQuery.isLoading) {
    return <p>Loading account details...</p>;
  }

  if (accountQuery.error) {
    return <p>An error has occurred: {accountQuery.error.message}</p>;
  }

  return (
    <div className="-mt-12 mx-auto max-w-2xl flex flex-col gap-4">
      <Title pagename="My Account" />

      <div className="flex flex-col gap-4 rounded-xl border border-default-200 p-6">
        <div>
          <p className="text-sm text-default-500">Email</p>
          <p className="font-medium">{accountInfo.email}</p>
        </div>
        <div>
          <p className="text-sm text-default-500">Account Type</p>
          <p className="font-medium">{user.accountType}</p>
        </div>
        {!isOwner && (
          <div>
            <p className="text-sm text-default-500">Address</p>
            <p className="font-medium">
              {"address" in accountInfo && accountInfo.address
                ? accountInfo.address
                : "No address found"}
            </p>
          </div>
        )}
      </div>

      <div className="flex flex-col gap-4 rounded-xl border border-default-200 p-6">
        <div>
          <p className="text-xl font-semibold">Change Password</p>
          <p className="text-sm text-default-500">
            Enter a new password for your account.
          </p>
        </div>

        <Form action={handleUpdatePassword} className="flex flex-col gap-4">
          {passwordErrors.length > 0 && (
            <div className="text-sm text-red-500 flex flex-col gap-1">
              {passwordErrors.map((error, index) => (
                <p key={index}>{error}</p>
              ))}
            </div>
          )}

          {passwordSuccess && (
            <p className="text-sm text-green-600">{passwordSuccess}</p>
          )}

          <TextField
            isRequired
            name="password"
            type="password"
            onChange={(value) => setPassword(value)}
          >
            <Label>New Password</Label>
            <Input placeholder="Enter your new password" value={password} />
          </TextField>

          <TextField
            isRequired
            name="confirmPassword"
            type="password"
            onChange={(value) => setConfirmPassword(value)}
          >
            <Label>Confirm Password</Label>
            <Input
              placeholder="Confirm your new password"
              value={confirmPassword}
            />
          </TextField>

          <div className="flex gap-2">
            <Button type="submit" isDisabled={updatePasswordMutation.isPending}>
              Save Password
            </Button>
            <Button
              type="button"
              variant="secondary"
              isDisabled={updatePasswordMutation.isPending}
              onPress={() => {
                setPassword("");
                setConfirmPassword("");
                setPasswordErrors([]);
                setPasswordSuccess(null);
              }}
            >
              Cancel
            </Button>
          </div>
        </Form>
      </div>

      {isCustomer && (
        <>
          <div className="flex flex-col gap-4 rounded-xl border border-default-200 p-6">
            <div>
              <p className="text-xl font-semibold">Change Address</p>
              <p className="text-sm text-default-500">
                Enter a new address for your account.
              </p>
            </div>

            <Form action={handleAddressUpdate} className="flex flex-col gap-4">
              <TextField
                isRequired
                name="address"
                type="text"
                onChange={(value) => setAddress(value)}
              >
                <Label>New Address</Label>
                <Input placeholder="Enter your new address" value={address} />
              </TextField>

              <div className="flex gap-2">
                <Button
                  type="submit"
                  isDisabled={updateAddressMutation.isPending}
                >
                  Save Address
                </Button>
                <Button
                  type="button"
                  variant="secondary"
                  isDisabled={updateAddressMutation.isPending}
                  onPress={() => {
                    setAddress(currentAddress);
                  }}
                >
                  Cancel
                </Button>
              </div>
            </Form>
          </div>

          <div className="flex flex-col gap-3 rounded-xl border border-red-200 p-6">
            <div>
              <p className="text-xl font-semibold text-red-600">
                Delete Account
              </p>
              <p className="text-sm text-default-500">
                This action is permanent and cannot be undone.
              </p>
            </div>

            {deleteErrors.length > 0 && (
              <div className="text-sm text-red-500 flex flex-col gap-1">
                {deleteErrors.map((error, index) => (
                  <p key={index}>{error}</p>
                ))}
              </div>
            )}

            {confirmingDelete ? (
              <div className="flex flex-col gap-2">
                <p className="text-sm font-medium">
                  Are you sure? This cannot be undone.
                </p>
                <div className="flex gap-2">
                  <Button
                    onPress={handleDeleteAccount}
                    isDisabled={deleteAccountMutation.isPending}
                  >
                    Yes, delete my account
                  </Button>
                  <Button
                    variant="secondary"
                    onPress={() => setConfirmingDelete(false)}
                    isDisabled={deleteAccountMutation.isPending}
                  >
                    Cancel
                  </Button>
                </div>
              </div>
            ) : (
              <Button onPress={() => setConfirmingDelete(true)}>
                Delete Account
              </Button>
            )}
          </div>
        </>
      )}
    </div>
  );
}
