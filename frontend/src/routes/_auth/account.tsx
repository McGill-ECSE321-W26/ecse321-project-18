import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/account")({
  beforeLoad: ({ context }) => {
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: "/login",
      });
    }
  },
  head: () => ({
    meta: [
      {
        title: "My Account | Fashion Store",
      },
    ],
  }),
  component: Account,
});

function Account() {
  return <h2>My Account</h2>;
}
