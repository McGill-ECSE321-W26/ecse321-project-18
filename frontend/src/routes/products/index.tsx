import { createFileRoute } from "@tanstack/react-router";
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";

import { Card } from "@heroui/react";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";

const queryClient = new QueryClient();

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      {
        title: "Home | Fashion Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <App />
    </QueryClientProvider>
  ),
});

function useClothingProducts() {
  return useQuery({
    queryKey: ["clothingProducts"],
    queryFn: () => getRequest("/clothingproduct"),
  });
}

function App() {
  const { isPending, error, data } = useClothingProducts();

  if (isPending) return <Skeleton />;

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
