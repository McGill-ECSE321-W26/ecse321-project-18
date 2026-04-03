import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/products/$productId")({
  head: () => ({
    meta: [
      {
        title: "<Product> | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Product />
    </QueryClientProvider>
  ),
});

function useClothingProduct(id: number) {
  return useQuery({
    queryKey: ["clothingProduct"],
    queryFn: () => getRequest(`/clothingproduct/${id}`),
  });
}

function Product() {
  const { productId }: { productId: number } = Route.useParams();

  const { isLoading, error, data } = useClothingProduct(productId);

  if (isLoading) return <Skeleton />;

  if (error) return "An error has occurred: " + error.message;

  return (
    <div>
      <h2>Product ID: {productId}</h2>
    </div>
  );
}
