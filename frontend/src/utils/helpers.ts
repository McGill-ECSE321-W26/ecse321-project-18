import { useQuery } from "@tanstack/react-query";
import { getRequest } from "./httpClient";
import type {
  AccountListResponse,
  ClothingProductResponse,
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

export function useAccounts() {
  return useQuery({
    queryKey: ["accounts"],
    queryFn: (): Promise<AccountListResponse> => getRequest("/account"),
  });
}
