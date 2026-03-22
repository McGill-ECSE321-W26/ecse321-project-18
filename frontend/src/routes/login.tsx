import { useMutation } from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { useState } from "react";

import type { AccountRequest, AccountResponse } from "#/types/api";
import { postRequest } from "#/utils/httpClient";
import { redirectForAccountType } from "#/utils/authorization";

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

const saveUserMutation = useMutation({
  mutationFn: async (account: AccountRequest): Promise<AccountResponse> => {
    return await postRequest("/account/login", account);
  },
});

function Login() {
  const { auth } = Route.useRouteContext();
  const navigate = Route.useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.SubmitEvent) => {
    e.preventDefault(); // prevent reload
    setIsLoading(true);
    setError("");

    try {
      // communicate with backend to login
      const response = await saveUserMutation.mutateAsync({
        email: email,
        password: password,
      });

      // update app context
    } catch (err) {
      setError("uh oh");
    }
  };

  return <></>;
}
