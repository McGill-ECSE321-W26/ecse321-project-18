import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute, redirect, useRouter } from "@tanstack/react-router";
import { useState } from "react";

import {
  Button,
  ErrorMessage,
  FieldError,
  Form,
  Input,
  Label,
  Spinner,
  TextField,
} from "@heroui/react";
import { IoMdCheckmark } from "react-icons/io";

import type { AccountRequest, AccountResponse } from "#/types/api";
import { postRequest } from "#/utils/httpClient";
import { redirectForAccountType } from "#/utils/authorization";
import { useAuth } from "#/auth";
import { sleep } from "#/utils/helpers";
import TopNav from "#/components/TopNav";
import { SubmitButton } from "#/components/SubmitButton";

const queryClient = new QueryClient();

export const Route = createFileRoute("/login")({
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
        title: "Login | Fashion Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Login />
    </QueryClientProvider>
  ),
});

const requestLogin = async (
  account: AccountRequest,
): Promise<AccountResponse> => {
  return await postRequest("/account/login", account);
};

function Login() {
  const auth = useAuth();
  const router = useRouter();
  const navigate = Route.useNavigate();
  const mutation = useMutation({ mutationFn: requestLogin });

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errors, setErrors] = useState<string[]>([]);

  const handleSubmit = async () => {
    setIsSubmitting(true);

    try {
      // communicate with backend to login
      const response: AccountResponse = await mutation.mutateAsync({
        email: email,
        password: password,
      });

      // update app context
      await auth.login(response);

      await router.invalidate();
      await sleep(50); // wait for auth state to update

      await navigate({ to: redirectForAccountType(response.accountType) });
    } catch (err) {
      // display errors and clear some input fields
      if (err instanceof AggregateError) {
        setErrors([...err.errors]);
      } else {
        setErrors([String(err)]);
      }

      setPassword("");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <>
      <TopNav account={undefined} />

      <main className="px-4 pb-8 pt-14">
        <h1 className="text-2xl font-bold text-center">Login</h1>
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

            {errors.map((error, index) => (
              <ErrorMessage key={index} className="text-sm">
                {error}
              </ErrorMessage>
            ))}

            <SubmitButton
              text="Login"
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
