import { useState } from "react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute, redirect, useRouter } from "@tanstack/react-router";
import {
  Checkbox,
  ErrorMessage,
  FieldError,
  Form,
  Input,
  Label,
  TextField,
} from "@heroui/react";

import type {
  AccountResponse,
  CustomerRequest,
  CustomerResponse,
  EmployeeResponse,
} from "#/types/api";
import { redirectForAccountType } from "#/utils/authorization";
import { postRequest, putRequest } from "#/utils/httpClient";
import TopNav from "#/components/TopNav";
import { SubmitButton } from "#/components/SubmitButton";
import { successToast } from "#/utils/helpers";
import { PasswordToggleInput } from "#/components/PasswordToggleInput";

const queryClient = new QueryClient();

export const Route = createFileRoute("/register")({
  beforeLoad: ({ context }) => {
    // redirect if already logged in
    if (context.auth.isAuthenticated) {
      throw redirect({
        to: redirectForAccountType(context.auth.user?.accountType),
      });
    }
  },
  head: () => ({
    meta: [
      {
        title: "Register | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Register />
    </QueryClientProvider>
  ),
});

const requestRegisterCustomer = async (
  account: CustomerRequest,
): Promise<CustomerResponse> => {
  // no error toast on register: display errors at end of form
  const accountResponse: AccountResponse = await postRequest(
    "/account/customer",
    account,
    false,
  );
  const { id } = accountResponse;

  // account created, now save address and keep loyalty points initialized at 0
  return await putRequest(
    `/account/customer/${id}`,
    {
      email: account.email,
      password: account.password,
      address: account.address,
      numOfLoyaltyPoints: account.numOfLoyaltyPoints,
    },
    false,
  );
};

const requestRegisterEmployee = async (
  account: CustomerRequest, // employees are also customers (but not vice versa)!
): Promise<EmployeeResponse> => {
  // no error toast on register: display errors at end of form
  const accountResponse: AccountResponse = await postRequest(
    "/account/employee",
    account,
    false,
  );
  const { id } = accountResponse;

  // account created, now save address and keep loyalty points initialized at 0
  return await putRequest(
    `/account/customer/${id}`,
    {
      email: account.email,
      password: account.password,
      address: account.address,
      numOfLoyaltyPoints: account.numOfLoyaltyPoints,
    },
    false,
  );
};

function Register() {
  const router = useRouter();
  const navigate = Route.useNavigate();
  const mutationCustomer = useMutation(
    {
      mutationFn: requestRegisterCustomer,
      onSuccess: () =>
        successToast(
          "Customer account created successfully",
          "Welcome to Stilton's Store!",
        ),
    },
    queryClient,
  );
  const mutationEmployee = useMutation(
    {
      mutationFn: requestRegisterEmployee,
      onSuccess: () =>
        successToast(
          "Employee account created successfully",
          "Welcome to Stilton's Store!",
        ),
    },
    queryClient,
  );

  const [email, setEmail] = useState<string>("");
  const [confirmedEmail, setConfirmedEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [address, setAddress] = useState<string>("");
  const [isEmployee, setIsEmployee] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [errors, setErrors] = useState<string[]>([]);

  const handleSubmit = async () => {
    setIsSubmitting(true);

    try {
      // employees are also customers (but not vice versa)!
      const account: CustomerRequest = {
        email: email,
        password: password,
        address: address,
        numOfLoyaltyPoints: 0,
      };

      // create employee or customer account
      isEmployee
        ? await mutationEmployee.mutateAsync(account)
        : await mutationCustomer.mutateAsync(account);

      await router.invalidate();

      await navigate({ to: "/login" });
    } catch (err) {
      // display errors and clear some input fields
      if (err instanceof AggregateError) {
        setErrors([...err.errors]);
      } else {
        setErrors([String(err)]);
      }

      setConfirmedEmail("");
      setPassword("");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <TopNav account={undefined} />

      <main className="px-4 pb-8 pt-14">
        <h1 className="text-center text-2xl font-bold">Register</h1>
        <br />
        <div className="flex justify-center items-center">
          <Form
            onSubmit={(e) => {
              e.preventDefault();
              handleSubmit();
            }}
            className="flex w-96 flex-col gap-4"
          >
            <TextField
              isRequired
              name="email"
              type="email"
              value={email}
              onChange={setEmail}
            >
              <Label>Email</Label>
              <Input placeholder="hi@example.com" />
              <FieldError />
            </TextField>
            <TextField
              isRequired
              name="confirmEmail"
              type="email"
              value={confirmedEmail}
              onChange={setConfirmedEmail}
              validate={(value) =>
                value === email ? null : "Email addresses do not match."
              }
            >
              <Label>Confirm email</Label>
              <Input placeholder="Re-enter your email" />
              <FieldError />
            </TextField>

            <PasswordToggleInput
              password={password}
              handleChange={setPassword}
            />

            <TextField
              isRequired
              name="address"
              type="text"
              value={address}
              onChange={setAddress}
            >
              <Label>Address</Label>
              <Input placeholder="3 Stilton Blvd" />
              <FieldError />
            </TextField>

            <Checkbox
              id="is-employee"
              name="isEmployee"
              value="off"
              onChange={setIsEmployee}
            >
              <Checkbox.Control>
                <Checkbox.Indicator />
              </Checkbox.Control>
              <Checkbox.Content>
                <Label htmlFor="is-employee">Create an employee account</Label>
              </Checkbox.Content>
            </Checkbox>

            <ErrorMessage className="text-sm">{errors}</ErrorMessage>

            <SubmitButton
              text="Register"
              isSubmitting={isSubmitting}
              isFullWidth={true}
              handleClick={() => setErrors([])}
            />
          </Form>
        </div>
      </main>
    </>
  );
}
