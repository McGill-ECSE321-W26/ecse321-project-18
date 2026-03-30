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

import type { AccountRequest, AccountResponse } from "#/types/api";
import { redirectForAccountType } from "#/utils/authorization";
import { postRequest } from "#/utils/httpClient";
import TopNav from "#/components/TopNav";
import { SubmitButton } from "#/components/SubmitButton";

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

  const [email, setEmail] = useState<string>("");
  const [confirmedEmail, setConfirmedEmail] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isEmployee, setIsEmployee] = useState<boolean>(false);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
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
              onChange={(value) => setEmail(value)}
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
              onChange={(value) => setConfirmedEmail(value)}
              validate={(value) =>
                value === email ? null : "Email addresses do not match."
              }
            >
              <Label>Confirm email</Label>
              <Input placeholder="Re-enter your email" />
              <FieldError />
            </TextField>
            <TextField
              isRequired
              name="password"
              type="password"
              minLength={8}
              maxLength={32}
              value={password}
              onChange={(value) => setPassword(value)}
            >
              <Label>Password</Label>
              <Input placeholder="Enter your password" />
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
