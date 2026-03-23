import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { useAuth } from "#/auth";
import { AccountType } from "#/types/api";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/orders")({
  beforeLoad: ({ context }) => {
    const userAccountType = context.auth.user?.accountType;

    if (userAccountType === AccountType.Owner) {
      throw redirect({
        to: "/admin/orders",
      });
    }
  },
  head: () => ({
    meta: [
      {
        title: "Orders | Fashion STore",
      },
    ],
  }),
  component: () => {
    <QueryClientProvider client={queryClient}>
      <Orders />
    </QueryClientProvider>;
  },
});

function Orders() {
  const auth = useAuth();

  /* to get account type (slightly different behavior if employee, vs. customer):
  
  auth.user?.accountType

  (user should never be null at this point, but you'll likely still be forced to check/be careful)
  */

  return <h2>Orders</h2>;
}
