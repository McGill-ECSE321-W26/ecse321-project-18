import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/admin/accounts")({
  head: () => ({
    meta: [
      {
        title: "Accounts | Fashion Store",
      },
    ],
  }),
  component: Accounts,
});

function Accounts() {
  return <h2 className="text-xl">Accounts</h2>;
}
