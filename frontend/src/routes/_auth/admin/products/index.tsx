import { Link, createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Card } from "@heroui/react";
import { useClothingProducts, useDeleteClothingProduct } from "#/utils/helpers";
import Skeleton from "#/components/Skeleton";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/products/")({
  head: () => ({
    meta: [
      {
        title: "Products | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <AdminProducts />
    </QueryClientProvider>
  ),
});

function AdminProducts() {
  const { data, isLoading, error } = useClothingProducts();
  const deleteMutation = useDeleteClothingProduct();

  if (isLoading) return <Skeleton />;
  if (error) return "Error: " + error.message;

  return (
    <div className="grid gap-4">
      {data.map(({ id, name }) => (
        <Card key={id}>
          <Card.Header className="flex justify-between">
            <span>{name}</span>
            <div className="flex gap-2">
              <Link
                to="/admin/products/$productId"
                params={{ productId: `${id}` }}
              >
                Edit
              </Link>

              <button
                onClick={() => deleteMutation.mutate(id)}
                disabled={deleteMutation.isPending}
              >
                {deleteMutation.isPending ? "Deleting..." : "Delete"}
              </button>
            </div>
          </Card.Header>
        </Card>
      ))}
    </div>
  );
}
