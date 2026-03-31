import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/cart/order")({
  head: () => ({
    meta: [
      {
        title: "Order | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Order />
    </QueryClientProvider>
  ),
});

function Order() {
  return <h2>Order</h2>;
}
