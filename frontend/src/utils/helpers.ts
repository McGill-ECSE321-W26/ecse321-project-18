import { useQuery } from "@tanstack/react-query";
import { getRequest } from "./httpClient";
import type {
  ClothingProductResponse,
  ShoppingCartListResponse,
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
