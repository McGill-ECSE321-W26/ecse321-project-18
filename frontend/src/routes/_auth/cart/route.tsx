import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";
import { AccountType } from "#/types/api";

export const Route = createFileRoute("/_auth/cart")({
  beforeLoad: ({ context }) => {
    const userAccountType = context.auth.user?.accountType;

    if (userAccountType === AccountType.Owner) {
      throw redirect({
        to: "/admin",
      });
    }
  },
  component: () => <Outlet />,
});
