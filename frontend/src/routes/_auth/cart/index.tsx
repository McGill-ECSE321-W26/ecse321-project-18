import { Button, Card, Form } from "@heroui/react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
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
import { deleteRequest, putRequest } from "#/utils/httpClient";
import { handleErrors, useCart, useClothingProducts } from "#/utils/helpers";

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

function Cart() {
  const auth = useAuth();
  const customerId = auth.user?.id;

  const [errors, setErrors] = useState<string[]>([]);
  const [isSubmitting, setIsSubmitting] = useState(false);

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

  const clearMutation = useMutation({
    mutationFn: async () =>
      await deleteRequest(`/account/customer/${customerId}/shoppingcartitem`),
    onSuccess: () => {
      queryClient.setQueryData(["shoppingCart"], () => []);
    },
  });

  const handleMutation = (
    cartItem: ShoppingCartItemResponse,
    newQuantity: number,
  ) => {
    setIsSubmitting(true);
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
      handleErrors(err, errors, setErrors);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    try {
      // redirect to cart/order page to confirm payment/info
    } catch (err) {
      handleErrors(err, errors, setErrors);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClear = async () => {
    setIsSubmitting(true);
    try {
      // redirect to cart/order page to confirm payment/info
      await clearMutation.mutateAsync();
    } catch (err) {
      handleErrors(err, errors, setErrors);
    } finally {
      setIsSubmitting(false);
    }
  };

  if (isCartLoading || isProductsLoading) return <Skeleton />;
  if (cartError) return "An error has occurred: " + cartError.message;
  if (productsError) return "An error has occurred: " + productsError.message;

  return (
    <>
      <h2>Cart</h2>
      {cartData && cartData.length != 0 ? (
        <div>
          <Form action={handleSubmit} onReset={handleClear}>
            {cartData.map((cartItem: ShoppingCartItemResponse) => {
              const product = productsData?.find(
                (p: ClothingProductResponse) =>
                  p.id === cartItem.clothingItem.clothingProductId,
              );
              return (
                <div key={cartItem.id}>
                  <Card>
                    {product && cartItem.quantity != 0 ? (
                      <div>
                        <div>
                          <img
                            src={
                              !product.image
                                ? product.image
                                : "" /* TODO add default image? */
                            }
                            loading="lazy"
                          />
                        </div>
                        <div>
                          <Card.Header>{product.name}</Card.Header>
                          <Card.Content>
                            <div>Size: {cartItem.clothingItem.size}</div>
                            <div>Colour: {cartItem.clothingItem.colour}</div>
                            <div>
                              <Button
                                onPress={() =>
                                  handleMutation(
                                    cartItem,
                                    cartItem.quantity - 1,
                                  )
                                }
                                isDisabled={isSubmitting}
                              >
                                -
                              </Button>
                              <div>{cartItem.quantity}</div>
                              <Button
                                onPress={() =>
                                  handleMutation(
                                    cartItem,
                                    cartItem.quantity + 1,
                                  )
                                }
                                isDisabled={isSubmitting}
                              >
                                +
                              </Button>
                            </div>
                          </Card.Content>
                        </div>
                      </div>
                    ) : (
                      <div>
                        <Card.Header>{"No product found."}</Card.Header>
                      </div>
                    )}
                  </Card>
                </div>
              );
            })}
            <Button type="reset" isDisabled={isSubmitting}>
              Clear
            </Button>
            <Button type="submit" isDisabled={isSubmitting}>
              Submit
            </Button>
          </Form>
        </div>
      ) : (
        <p>No clothing products match these filters.</p>
      )}
    </>
  );
}
