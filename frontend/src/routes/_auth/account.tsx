import { createFileRoute, redirect } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/account")({
  head: () => ({
    meta: [
      {
        title: "My Account | Stilton's Store",
      },
    ],
  }),
  component: Account,
});

function Account() {
  return <h2>My Account</h2>;
}
