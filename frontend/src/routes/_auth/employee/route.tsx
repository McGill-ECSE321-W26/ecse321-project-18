import { Outlet, createFileRoute, redirect } from "@tanstack/react-router";
import { AccountType } from "#/types/api";
import { redirectForAccountType } from "#/utils/authorization";

export const Route = createFileRoute("/_auth/employee")({
  beforeLoad: ({ context }) => {
    const userAccountType = context.auth.user?.accountType;

    if (userAccountType !== AccountType.Employee) {
      throw redirect({
        to: redirectForAccountType(userAccountType),
      });
    }
  },
  component: () => <Outlet />,
});
