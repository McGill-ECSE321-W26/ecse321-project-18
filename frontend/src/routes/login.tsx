import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/login")({
  head: () => ({
    meta: [
      {
        title: "Login | Fashion Store",
      },
    ],
  }),
  component: Login,
});

function Login() {
  return <></>;
}
