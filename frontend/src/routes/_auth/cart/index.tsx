import { Card } from "@heroui/react";
import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import type {
  ClothingProductResponse,
  ShoppingCartItemResponse,
} from "#/types/api";
import { useAuth } from "#/auth";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";

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
    queryFn: () =>
      getRequest(`/account/customer/${customerId}/shoppingcartitem`),
  });
}

function useClothingProducts() {
  return useQuery({
    queryKey: ["clothingProducts"],
    queryFn: () => getRequest("/clothingproduct"),
  });
}

function Cart() {
  const auth = useAuth();
  const customerId = auth.user?.id;

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
                {product ? (
                  <div>
                    <Card.Header>{product.name}</Card.Header>
                    <Card.Content>
                      <div>Quantity: {cartItem.quantity}</div>
                      <div>Size: {cartItem.clothingItem.size}</div>
                      <div>Colour: {cartItem.clothingItem.colour}</div>
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
