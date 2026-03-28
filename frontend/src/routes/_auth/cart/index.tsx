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
  ShoppingCartListResponse,
  ShoppingCartResponse,
} from "#/types/api";
import { useAuth } from "#/auth";
import Skeleton from "#/components/Skeleton";
import { deleteRequest, putRequest } from "#/utils/httpClient";
import { handleErrors, useCart, useClothingProducts } from "#/utils/helpers";

const defaultImg = "/logo512.png";

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
  const navigate = Route.useNavigate();

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
    }): Promise<ShoppingCartResponse> =>
      await putRequest(
        `/account/customer/${customerId}/shoppingcartitem/${cartItemId}`,
        updateItem,
      ),
    onSuccess: (newItem: ShoppingCartResponse) => {
      queryClient.setQueryData(
        ["shoppingCart"],
        (oldData: ShoppingCartListResponse) => {
          const newList = oldData.shoppingCartList.map(
            (item: ShoppingCartItemResponse) =>
              item.id === newItem.shoppingCartItem.id
                ? newItem.shoppingCartItem
                : item,
          );
          return { shoppingCartList: newList, price: newItem.price };
        },
      );
    },
  });

  const deleteMutation = useMutation({
    mutationFn: async ({
      cartItemId,
    }: {
      cartItemId: number;
    }): Promise<ShoppingCartResponse> =>
      await deleteRequest(
        `/account/customer/${customerId}/shoppingcartitem/${cartItemId}`,
      ),
    onSuccess: (data: ShoppingCartResponse, variables) => {
      queryClient.setQueryData(
        ["shoppingCart"],
        (oldData: ShoppingCartListResponse) => {
          const newList = oldData.shoppingCartList.filter(
            (item: ShoppingCartItemResponse) =>
              item.id !== variables.cartItemId,
          );
          return { shoppingCartList: newList, price: data.price };
        },
      );
    },
  });

  const clearMutation = useMutation({
    mutationFn: async () =>
      await deleteRequest(`/account/customer/${customerId}/shoppingcartitem`),
    onSuccess: () => {
      queryClient.setQueryData(["shoppingCart"], () => {
        return { shoppingCartList: [], price: 0 };
      });
    },
  });

  const handleMutation = async (
    cartItem: ShoppingCartItemResponse,
    newQuantity: number,
  ) => {
    setIsSubmitting(true);
    try {
      if (newQuantity <= 0) {
        await deleteMutation.mutateAsync({ cartItemId: cartItem.id });
      } else {
        await updateMutation.mutateAsync({
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
      navigate({ to: "/cart/order" });
    } catch (err) {
      handleErrors(err, errors, setErrors);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClear = async () => {
    setIsSubmitting(true);
    try {
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
      {cartData?.shoppingCartList && cartData.shoppingCartList.length != 0 ? (
        <div>
          <Form
            action={handleSubmit}
            onReset={handleClear}
            className="grid grid-cols-[1fr] md:grid-cols-[3fr_1fr] gap-8 items-stretch"
          >
            {/* Left side */}
            <div className="grid grid-cols-1 gap-4 overflow-y-auto">
              {cartData.shoppingCartList.map(
                (cartItem: ShoppingCartItemResponse) => {
                  const product = productsData?.find(
                    (p: ClothingProductResponse) =>
                      p.id === cartItem.clothingItem.clothingProductId,
                  );
                  return (
                    <Card
                      key={cartItem.id}
                      className="border-black border shadow-none"
                    >
                      {product && cartItem.quantity != 0 ? (
                        <div className="grid grid-cols-[80px_1fr_130px] items-center gap-4 p-3">
                          {/* Image */}
                          <div className="h-20 w-20 overflow-hidden rounded-xl">
                            <img
                              src={
                                product.image || defaultImg
                              } /* TODO add default image? */
                              alt={product.name}
                              className="h-full w-full object-cover"
                              loading="lazy"
                            />
                          </div>

                          {/* Information */}
                          <div className="grid grid-rows-[auto_auto] gap-1 min-w-0">
                            <h4 className="font-bold text-lg">
                              {product.name}
                            </h4>
                            <div>
                              <p>Size: {cartItem.clothingItem.size}</p>
                              <p>Colour: {cartItem.clothingItem.colour}</p>
                            </div>
                          </div>

                          {/* Quantity/buttons */}
                          <div className="grid grid-cols-[32px_1fr_32px] items-center bg-default-100 text-center overflow-hidden border border-black rounded-full">
                            <Button
                              onPress={() =>
                                handleMutation(cartItem, cartItem.quantity - 1)
                              }
                              isDisabled={isSubmitting}
                              size="sm"
                              className="rounded-l-full border-r"
                            >
                              -
                            </Button>
                            <div>{cartItem.quantity}</div>
                            <Button
                              onPress={() =>
                                handleMutation(cartItem, cartItem.quantity + 1)
                              }
                              isDisabled={isSubmitting}
                              size="sm"
                              className="rounded-r-full border-l"
                            >
                              +
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <div>
                          <Card.Header>{"No product found."}</Card.Header>
                        </div>
                      )}
                    </Card>
                  );
                },
              )}
            </div>

            <div className="col-span-1">
              <Card className="grid border border-black h-full">
                <div className="grid content-start gap-2">
                  <h4 className="font-bold text-lg">Total</h4>
                  <p>${cartData.price}</p>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 border-t border-black pt-4">
                  <Button
                    type="reset"
                    isDisabled={isSubmitting}
                    className="bg-red-500"
                  >
                    Clear
                  </Button>
                  <Button type="submit" isDisabled={isSubmitting}>
                    Proceed
                  </Button>
                </div>
              </Card>
            </div>
          </Form>
        </div>
      ) : (
        <p>No items in cart.</p>
      )}
    </>
  );
}
