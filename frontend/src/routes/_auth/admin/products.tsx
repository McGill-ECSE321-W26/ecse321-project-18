import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/admin/products")({
  head: () => ({
    meta: [
      {
        title: "Products | Stilton's Store",
      },
    ],
  }),
  component: Products,
});

function Products() {
  return <h2 className="text-xl">Products</h2>;
}
