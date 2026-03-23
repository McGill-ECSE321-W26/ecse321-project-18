import { useMutation } from "@tanstack/react-query";
import { createFileRoute, redirect, useRouter } from "@tanstack/react-router";
import { useState } from "react";

import { Button, Form, Input, Label, TextField } from "@heroui/react";
import { Check } from "lucide-react";

import type { AccountRequest, AccountResponse } from "#/types/api";
import { AccountType } from "#/types/api";
import { postRequest } from "#/utils/httpClient";
import { redirectForAccountType } from "#/utils/authorization";
import { useAuth } from "#/auth";
import { sleep } from "#/utils/helpers";

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
  component: Login,
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

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (formData: FormData) => {
    "use server";
    setIsSubmitting(true);
    setError("");

    try {
      // communicate with backend to login
      const mutation = useMutation({ mutationFn: requestLogin });
      const response: AccountResponse = await mutation.mutateAsync({
        email: email,
        password: password,
      });

      // update app context
      await auth.login(response);

      await router.invalidate();
      await sleep(1); // wait for auth state to update

      await navigate({ to: redirectForAccountType(response.accountType) });
    } catch (err) {
      setError("uh oh");
      console.log(err);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
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
          <Button type="submit" isDisabled={!isSubmitting}>
            <Check />
            Submit
          </Button>
          <Button type="reset" variant="secondary" isDisabled={!isSubmitting}>
            Reset
          </Button>
        </div>
      </Form>
    </div>
  );
}
