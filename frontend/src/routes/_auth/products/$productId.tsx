import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";
import { updateItemStock, useDeleteClothingItem } from "#/utils/helpers";

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
    queryKey: ["clothingProduct", id],
    queryFn: () => getRequest(`/clothingproduct/${id}`),
  });
}

function Product() {
  const { productId } = Route.useParams();
  const id = Number(productId);

  const { isLoading, error, data, refetch } = useClothingProduct(id);
  const deleteItemMutation = useDeleteClothingItem(id);

  if (isLoading) return <Skeleton />;
  if (error) return "An error has occurred: " + error.message;

  const items = (data?.clothingItems ?? []).sort((a, b) => a.id - b.id);

  async function handleUpdateStock(item, newStock) {
    console.log("Updating stock...", { item, newStock });

    try {
      await updateItemStock(id, item, newStock);
      await refetch(); // refresh UI
      console.log("Stock updated!");
    } catch (err) {
      console.error("Failed to update stock", err);
    }
  }

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold">{data.name}</h2>

      <img
        src={data.image}
        alt={data.name}
        className="w-48 h-48 object-cover rounded"
      />

      <p className="text-lg">Price: ${data.price}</p>

      <h3 className="text-xl font-semibold mt-4">Items</h3>

      <ul className="space-y-3 pl-6">
        {items.map((item) => (
          <li key={item.id} className="flex items-center gap-4">
            <span>
              Size: {item.size} — Colour: {item.colour}
            </span>

            <input
              type="number"
              defaultValue={item.numInStock}
              className="border px-2 py-1 w-20"
              onChange={(e) => (item._newStock = Number(e.target.value))}
            />

            <button
              className="px-3 py-1 bg-blue-600 text-white rounded"
              onClick={() =>
                handleUpdateStock(item, item._newStock ?? item.numInStock)
              }
            >
              Update
            </button>

            <button
              className="px-3 py-1 bg-red-600 text-white rounded"
              disabled={deleteItemMutation.isPending}
              onClick={() => deleteItemMutation.mutate(item.id)}
            >
              {deleteItemMutation.isPending ? "Deleting..." : "Delete"}
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}
