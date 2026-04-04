import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import { Button, Input } from "@heroui/react";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import type {
  ClothingItemResponse,
  ClothingProductResponse,
} from "#/types/api";
import { getRequest } from "#/utils/httpClient";
import { updateItemStock, useDeleteClothingItem } from "#/utils/helpers";

const queryClient = new QueryClient();
const defaultImg = "/stiltonslogo.png";

export const Route = createFileRoute("/_auth/admin/products/$productId")({
  loader: async ({ params }): Promise<ClothingProductResponse> => {
    return await getRequest<ClothingProductResponse>(
      `/clothingproduct/${params.productId}`,
    );
  },
  head: ({ loaderData }) => ({
    meta: [
      {
        title: `Manage ${loaderData?.name ?? "Product"} | Stilton's Store`,
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Product />
    </QueryClientProvider>
  ),
});

function useClothingProduct(id: number, initialData?: ClothingProductResponse) {
  return useQuery({
    queryKey: ["clothingProduct", id],
    queryFn: (): Promise<ClothingProductResponse> =>
      getRequest(`/clothingproduct/${id}`),
    initialData,
  });
}

function Product() {
  const { productId } = Route.useParams();
  const id = Number(productId);
  const [editedStock, setEditedStock] = useState<Record<number, number>>({});

  const initialData = Route.useLoaderData();
  const { data } = useClothingProduct(id, initialData);
  const deleteItemMutation = useDeleteClothingItem(id);

  if (!data) {
    return (
      <div className="text-center text-red-600 font-semibold">
        Data for product with ID {id} could not be fetched.
      </div>
    );
  }

  const items = data.clothingItems.sort((a, b) => a.id - b.id);

  async function handleUpdateStock(
    item: ClothingItemResponse,
    newStock: number,
  ) {
    await updateItemStock(id, item, newStock);
  }

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold">{data.name}</h2>

      <img
        src={data.image && data.image !== "string" ? data.image : defaultImg}
        alt={data.name}
        className="w-48 h-48 object-cover rounded"
        onError={(e) => {
          e.currentTarget.src = defaultImg;
        }}
      />

      <p className="text-lg">Price: ${data.price}</p>

      <h3 className="text-xl font-semibold mt-4">Items</h3>

      <ul className="space-y-3 pl-6">
        {items.map((item) => (
          <li key={item.id} className="flex items-center gap-4">
            <span>
              Size: {item.size} — Colour: {item.colour}
            </span>

            <Input
              type="number"
              defaultValue={item.numInStock}
              className="w-24"
              onChange={(e) =>
                setEditedStock((prev) => ({
                  ...prev,
                  [item.id]: Number(e.target.value),
                }))
              }
              aria-label="Stock quantity"
            />

            <Button
              size="sm"
              className="bg-blue-600 text-white hover:bg-blue-700"
              onPress={() =>
                handleUpdateStock(item, editedStock[item.id] ?? item.numInStock)
              }
            >
              Update
            </Button>

            <Button
              size="sm"
              isDisabled={deleteItemMutation.isPending}
              onPress={() => deleteItemMutation.mutate(item.id)}
              className="bg-red-600 text-white hover:bg-red-700"
            >
              {deleteItemMutation.isPending ? "Deleting..." : "Delete"}
            </Button>
          </li>
        ))}
      </ul>
    </div>
  );
}
