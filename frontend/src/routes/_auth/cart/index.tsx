import { Button, Card } from "@heroui/react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import type {
  ClothingProductResponse,
  ShoppingCartItemRequest,
  ShoppingCartItemResponse,
} from "#/types/api";
import { useAuth } from "#/auth";
import Skeleton from "#/components/Skeleton";
import { deleteRequest, getRequest, putRequest } from "#/utils/httpClient";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/cart/")({
  head: () => ({
    meta: [
      {
        title: "Cart | Fashion Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Cart />
    </QueryClientProvider>
  ),
});

function useCart(customerId: number) {
  return useQuery({
    queryKey: ["shoppingCart"],
    queryFn: (): Promise<ShoppingCartItemResponse[]> =>
      getRequest(`/account/customer/${customerId}/shoppingcartitem`),
  });
}

function useClothingProducts() {
  return useQuery({
    queryKey: ["clothingProducts"],
    queryFn: (): Promise<ClothingProductResponse[]> =>
      getRequest("/clothingproduct"),
  });
}

function Cart() {
  const auth = useAuth();
  const customerId = auth.user?.id;

  const [errors, setErrors] = useState<any[]>([]);

  if (!customerId) return "An error has occurred: Customer ID not found.";

  const {
    isLoading: isCartLoading,
    error: cartError,
    data: cartData,
  } = useCart(customerId);
  const {
    isLoading: isProductsLoading,
    error: productsError,
    data: productsData,
  } = useClothingProducts();

  if (isCartLoading || isProductsLoading) return <Skeleton />;
  if (cartError) return "An error has occurred: " + cartError.message;
  if (productsError) return "An error has occurred: " + productsError.message;

  const updateMutation = useMutation({
    mutationFn: async ({
      cartItemId,
      updateItem,
    }: {
      cartItemId: number;
      updateItem: ShoppingCartItemRequest;
    }): Promise<ShoppingCartItemResponse> =>
      await putRequest(
        `/account/customer/${customerId}/shoppingcartitem/${cartItemId}`,
        updateItem,
      ),
    onSuccess: (newItem: ShoppingCartItemResponse) => {
      queryClient.setQueryData(["shoppingCart"], (oldData: any) => {
        if (!oldData) return [];
        return oldData.map((item: ShoppingCartItemResponse) =>
          item.id === newItem.id ? newItem : item,
        );
      });
    },
  });
  const deleteMutation = useMutation({
    mutationFn: async ({ cartItemId }: { cartItemId: number }) =>
      await deleteRequest(
        `/account/customer/${customerId}/shoppingcartitem/${cartItemId}`,
      ),
    onSuccess: (_data, variables) => {
      queryClient.setQueryData(["shoppingCart"], (oldData: any) => {
        if (!oldData) return [];
        return oldData.filter(
          (item: ShoppingCartItemResponse) => item.id !== variables.cartItemId,
        );
      });
    },
  });
  const handleMutation = (
    cartItem: ShoppingCartItemResponse,
    newQuantity: number,
  ) => {
    try {
      if (newQuantity <= 0) {
        deleteMutation.mutateAsync({ cartItemId: cartItem.id });
      } else {
        updateMutation.mutateAsync({
          cartItemId: cartItem.id,
          updateItem: {
            quantity: newQuantity,
            clothingItemId: cartItem.clothingItem.id,
          },
        });
      }
    } catch (err) {
      if (err instanceof AggregateError) {
        setErrors([...errors, ...err.errors]);
      } else {
        const errorMessage = err instanceof Error ? err.message : String(err);
        setErrors([...errors, errorMessage]);
      }
      console.log(err);
    }
  };

  return (
    <>
      <h2>Cart</h2>
      {cartData ? (
        <div>
          {cartData.map((cartItem: ShoppingCartItemResponse) => {
            const product = productsData?.find(
              (p: ClothingProductResponse) =>
                p.id === cartItem.clothingItem.clothingProductId,
            );
            return (
              <Card key={cartItem.id}>
                {product && cartItem.quantity != 0 ? (
                  <div>
                    <Card.Header>{product.name}</Card.Header>
                    <Card.Content>
                      <div>Size: {cartItem.clothingItem.size}</div>
                      <div>Colour: {cartItem.clothingItem.colour}</div>
                      <div>
                        <Button
                          onPress={() =>
                            handleMutation(cartItem, cartItem.quantity - 1)
                          }
                        >
                          -
                        </Button>
                        <div>{cartItem.quantity}</div>
                        <Button
                          onPress={() =>
                            handleMutation(cartItem, cartItem.quantity + 1)
                          }
                        >
                          +
                        </Button>
                      </div>
                    </Card.Content>
                  </div>
                ) : (
                  <div>
                    <Card.Header>{"No product found."}</Card.Header>
                  </div>
                )}
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
