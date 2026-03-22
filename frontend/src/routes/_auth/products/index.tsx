import { createFileRoute } from "@tanstack/react-router";
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";

import { Card } from "@heroui/react";

import type { ClothingProductResponse } from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/products/")({
  head: () => ({
    meta: [
      {
        title: "Products | Fashion Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Products />
    </QueryClientProvider>
  ),
});

function useClothingProducts() {
  return useQuery({
    queryKey: ["clothingProducts"],
    queryFn: () => getRequest("/clothingproduct"),
  });
}

function Products() {
  const { isLoading, error, data } = useClothingProducts();

  if (isLoading) return <Skeleton />;

  if (error) return "An error has occurred: " + error.message;

  return (
    <>
      {data ? (
        <div>
          {data.map((clothingProduct: ClothingProductResponse) => {
            return (
              <Card key={clothingProduct.id}>
                <Card.Header>{clothingProduct.name}</Card.Header>
              </Card>
            );
          })}
        </div>
      ) : (
        <p>No clothing products match these filters.</p>
      )}
    </>
  );
}
