import { Link, createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Card } from "@heroui/react";
import { useClothingProducts } from "#/utils/helpers";
import Skeleton from "#/components/Skeleton";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/products")({
  head: () => ({
    meta: [{ title: "Products | Fashion Store" }],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <AdminProducts />
    </QueryClientProvider>
  ),
});

function AdminProducts() {
  const { data, isLoading, error } = useClothingProducts();

  if (isLoading) return <Skeleton />;
  if (error) return "Error: " + error.message;

  return (
    <div className="grid gap-4">
      {data.map((product) => (
        <Card key={product.id}>
          <Card.Header className="flex justify-between">
            <span>{product.name}</span>
            <div className="flex gap-2">
              <Link to={`/products/${product.id}`}>Edit</Link>
              <button onClick={() => deleteProduct(product.id)}>Delete</button>
            </div>
          </Card.Header>
        </Card>
      ))}
    </div>
  );
}
