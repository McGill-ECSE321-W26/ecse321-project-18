import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/cart/")({
  head: () => ({
    meta: [
      {
        title: "Cart | Fashion Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Cart />
    </QueryClientProvider>
  ),
});

function Cart() {
  return <h2>Cart</h2>;
}
