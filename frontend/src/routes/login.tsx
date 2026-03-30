import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute, redirect, useRouter } from "@tanstack/react-router";
import { useState } from "react";

import { Button, Form, Input, Label, TextField } from "@heroui/react";
import { Check } from "lucide-react";

import type { AccountRequest, AccountResponse } from "#/types/api";
import { postRequest } from "#/utils/httpClient";
import { redirectForAccountType } from "#/utils/authorization";
import { useAuth } from "#/auth";
import { sleep } from "#/utils/helpers";
import TopNav from "#/components/TopNav";
import { updateErrors } from "#/utils/error";

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
    } catch (err: any) {
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

            <div className="flex gap-2">
              <Button type="submit" isDisabled={isSubmitting}>
                <Check />
                Login
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
