import { useQuery } from "@tanstack/react-query";
import { toast } from "@heroui/react";
import { getRequest, getRequestWithParams } from "./httpClient";
import type {
  AccountListResponse,
  ClothingProductResponse,
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
