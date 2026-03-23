import { createFileRoute, redirect } from "@tanstack/react-router";
import { AccountType } from "#/types/api";
import { redirectForAccountType } from "#/utils/authorization";

export const Route = createFileRoute("/_auth/admin/")({
  beforeLoad: ({ context }) => {
    const userAccountType = context.auth.user?.accountType;

    if (userAccountType !== AccountType.Owner) {
      throw redirect({
        to: redirectForAccountType(userAccountType),
      });
    }
  },
  head: () => ({
    meta: [
      {
        title: "Dashboard | Fashion Store",
      },
    ],
  }),
  component: AdminDashboard,
});

function AdminDashboard() {
  return <h2 className="text-xl">Dashboard</h2>;
}
