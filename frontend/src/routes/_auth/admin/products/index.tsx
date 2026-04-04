import { Link, createFileRoute } from "@tanstack/react-router";
import { Button, Card } from "@heroui/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { useClothingProducts, useDeleteClothingProduct } from "#/utils/helpers";
import Skeleton from "#/components/Skeleton";
import Title from "#/components/Title";

const queryClient = new QueryClient();
const defaultImg = "/stiltonslogo.png";

export const Route = createFileRoute("/_auth/admin/products/")({
  head: () => ({
    meta: [
      {
        title: "Manage products | Stilton's Store",
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
  if (!data) {
    return (
      <div className="text-center text-red-600 font-semibold">
        Data for products could not be fetched.
      </div>
    );
  }

  return (
    <div className="-mt-12 flex flex-col gap-4">
      <Title pagename="Stilton's Store's Accounts" />
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {data.map(({ id, name, image }) => {
          return (
            <Card key={id}>
              <Card.Header className="flex justify-between items-center">
                <span className="font-medium">{name}</span>
                <img
                  src={image && image !== "string" ? image : defaultImg}
                  alt={name}
                  className="w-48 h-48 object-cover rounded"
                  onError={(e) => {
                    e.currentTarget.src = defaultImg;
                  }}
                />
                <div className="flex gap-3 items-center">
                  <Link
                    to="/admin/products/$productId"
                    params={{ productId: `${id}` }}
                  >
                    <Button size="sm" isDisabled={deleteMutation.isPending}>
                      Edit
                    </Button>
                  </Link>
                  <Button
                    size="sm"
                    isDisabled={deleteMutation.isPending}
                    onPress={() => deleteMutation.mutate(id)}
                    className="bg-red-600 text-white hover:bg-red-700"
                  >
                    {deleteMutation.isPending ? "Deleting..." : "Delete"}
                  </Button>
                </div>
              </Card.Header>
            </Card>
          );
        })}
      </div>
    </div>
  );
}
