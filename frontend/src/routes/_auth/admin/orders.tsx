import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/admin/orders")({
  head: () => ({
    meta: [
      {
        title: "Orders | Fashion Store",
      },
    ],
  }),
  component: Orders,
});

function Orders() {
  return <h2 className="text-xl">Orders</h2>;
}
