import { useState } from "react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute, redirect, useRouter } from "@tanstack/react-router";
import { Button, Checkbox, Form, Input, Label, TextField } from "@heroui/react";
import { Check } from "lucide-react";

import type { AccountRequest, AccountResponse } from "#/types/api";
import { redirectForAccountType } from "#/utils/authorization";
import { postRequest } from "#/utils/httpClient";
import TopNav from "#/components/TopNav";
import { updateErrors } from "#/utils/error";

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
        title: "Register | Fashion Store",
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
  account: AccountRequest,
): Promise<AccountResponse> => {
  return await postRequest("/account/customer", account);
};

const requestRegisterEmployee = async (
  account: AccountRequest,
): Promise<AccountResponse> => {
  return await postRequest("/account/employee", account);
};

function Register() {
  const router = useRouter();
  const navigate = Route.useNavigate();
  const mutationCustomer = useMutation({ mutationFn: requestRegisterCustomer });
  const mutationEmployee = useMutation({ mutationFn: requestRegisterEmployee });

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isEmployee, setIsEmployee] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState<string[]>([]);

  const handleSubmit = async () => {
    setIsSubmitting(true);

    try {
      const account: AccountRequest = {
        email: email,
        password: password,
      };

      // create employee or customer account
      isEmployee
        ? await mutationEmployee.mutateAsync(account)
        : await mutationCustomer.mutateAsync(account);

      await router.invalidate();

      await navigate({ to: "/login" });
    } catch (err) {
      // TODO: robust error handling
      updateErrors(err, errors, setErrors);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <TopNav account={undefined} />

      <main className="px-4 pb-8 pt-14">
        <div className="flex justify-center items-center">
          <Form action={handleSubmit} className="flex w-96 flex-col gap-4">
            <TextField
              isRequired
              name="email"
              type="email"
              onChange={(value) => setEmail(value)}
            >
              <Label>Email</Label>
              <Input placeholder="hi@example.com" />
            </TextField>
            <TextField
              isRequired
              name="password"
              type="password"
              onChange={(value) => setPassword(value)}
            >
              <Label>Password</Label>
              <Input placeholder="Enter your password" />
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

            <div className="flex gap-2">
              <Button type="submit" isDisabled={isSubmitting}>
                <Check />
                Register
              </Button>
              <Button
                type="reset"
                variant="secondary"
                isDisabled={isSubmitting}
              >
                Reset
              </Button>
            </div>
          </Form>
        </div>
      </main>
    </>
  );
}
