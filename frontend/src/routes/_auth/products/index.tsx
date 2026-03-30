import { createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { Card } from "@heroui/react";

import { useState } from "react";
import type {
  ClothingColour,
  ClothingProductResponse,
  ClothingSize,
} from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { useClothingProducts } from "#/utils/helpers";

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

function Products() {
  const [searchName, useSearchName] = useState<string>("");
  const [sizeFilters, useSizeFilters] = useState<ClothingSize[] | null>(null);
  const [colourFilters, useColourFilters] = useState<ClothingColour[] | null>(
    null,
  );

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
