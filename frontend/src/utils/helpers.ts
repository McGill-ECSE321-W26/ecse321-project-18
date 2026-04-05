import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "@heroui/react";
import {
  deleteRequest,
  getRequest,
  getRequestWithParams,
  putRequest,
} from "./httpClient";
import type {
  AccountListResponse,
  ClothingColour,
  ClothingProductResponse,
  ClothingSize,
  CustomerResponse,
  OrderResponse,
  ShoppingCartListResponse,
} from "#/types/api";

export const sleep = async (ms: number) => {
  return new Promise((resolve) => setTimeout(resolve, ms));
};

export function useCart(customerId: number) {
  return useQuery({
    queryKey: ["shoppingCart"],
    queryFn: (): Promise<ShoppingCartListResponse> =>
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
/* sizes and colours should really be ClothingSize[] and ClothingColour[], respectively.
  UI components may not be able to enforce this stricter typing due to
    the UI library expecting looser types, so be careful!
*/
export function useMatchingClothingProducts(
  name: string,
  sizes: string[],
  colours: string[],
) {
  return useQuery({
    queryKey: ["matchingClothingProducts"],
    queryFn: (): Promise<ClothingProductResponse[]> =>
      getRequestWithParams("/clothingproduct", {
        name: name,
        sizes: sizes,
        colours: colours,
      }),
  });
}

export function useAccounts() {
  return useQuery({
    queryKey: ["accounts"],
    queryFn: (): Promise<AccountListResponse> => getRequest("/account"),
  });
}

export function useOrders() {
  return useQuery({
    queryKey: ["orders"],
    queryFn: (): Promise<OrderResponse[]> => getRequest("/order"),
  });
}

export function useCustomer(id: number) {
  return useQuery({
    queryKey: ["customer"],
    queryFn: (): Promise<CustomerResponse> =>
      getRequest(`/account/customer/${id}`),
  });
}

export function useCustomerOrders(id: number) {
  return useQuery({
    queryKey: ["customerOrders"],
    queryFn: (): Promise<OrderResponse[]> =>
      getRequest(`/account/customer/${id}/order`),
  });
}

export const successToast = (message: string, desc?: string) => {
  toast.success(message, {
    actionProps: {
      children: "Dismiss",
      onPress: () => toast.clear(),
      variant: "tertiary",
      className: "",
    },
    description: desc,
    timeout: 10000,
  });
};
