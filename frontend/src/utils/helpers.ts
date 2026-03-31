import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { deleteRequest, getRequest, putRequest } from "./httpClient";
import type {
  ClothingColour,
  ClothingProductResponse,
  ClothingSize,
  ShoppingCartItemResponse,
} from "#/types/api";

export const sleep = async (ms: number) => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

export const handleErrors = (
  err: any,
  errors: string[],
  setErrors: React.Dispatch<React.SetStateAction<string[]>>,
) => {
  if (err instanceof AggregateError) {
    setErrors([...errors, ...err.errors]);
  } else {
    const errorMessage = err instanceof Error ? err.message : String(err);
    setErrors([...errors, errorMessage]);
  }
};

export function useCart(customerId: number) {
  return useQuery({
    queryKey: ["shoppingCart"],
    queryFn: (): Promise<ShoppingCartItemResponse[]> =>
      getRequest(`/account/customer/${customerId}/shoppingcartitem`),
  });
}

export function useClothingProducts() {
  return useQuery({
    queryKey: ["clothingProducts"],
    queryFn: (): Promise<ClothingProductResponse[]> =>
      getRequest("/clothingproduct"),
  });
}

export function deleteClothingProduct(productId: number) {
  return deleteRequest(`/clothingproduct/${productId}`);
}

export function useDeleteClothingProduct() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: deleteClothingProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["clothingProducts"] });
    },
  });
}

export function deleteClothingItem(productId: number, itemId: number) {
  return deleteRequest(`/clothingproduct/${productId}/clothingitem/${itemId}`);
}

export function useDeleteClothingItem(productId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (itemId: number) => deleteClothingItem(productId, itemId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["clothingProduct", productId],
      });
    },
  });
}

export function updateItemStock(
  productId: number,
  item: {
    id: number;
    size: ClothingSize;
    colour: ClothingColour;
    numInStock: number;
  },
  newStock: number,
) {
  return putRequest(`/clothingproduct/${productId}/clothingitem/${item.id}`, {
    size: item.size,
    colour: item.colour,
    clothingProductId: productId,
    numInStock: newStock,
  });
}
