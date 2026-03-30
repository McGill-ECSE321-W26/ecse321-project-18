import { Button, Card, Form } from "@heroui/react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { useState } from "react";
import { FaRegTrashAlt } from "react-icons/fa";
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
import { useCart, useClothingProducts } from "#/utils/helpers";

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
      // save updated item into cache
      queryClient.setQueryData(
        ["shoppingCart"],
        (oldData: ShoppingCartListResponse) => {
          const newList = oldData.shoppingCartList.map(
            (item: ShoppingCartItemResponse) =>
              // only update the updated shopping cart item in cache
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
    onSuccess: (
      response: ShoppingCartResponse,
      variables: { cartItemId: number },
    ) => {
      // filter out the deleted shopping cart item from cache
      queryClient.setQueryData(
        ["shoppingCart"],
        (oldData: ShoppingCartListResponse) => {
          const newList = oldData.shoppingCartList.filter(
            (item: ShoppingCartItemResponse) =>
              item.id !== variables.cartItemId,
          );
          return { shoppingCartList: newList, price: response.price };
        },
      );
    },
  });

  const clearMutation = useMutation({
    mutationFn: async (): Promise<ShoppingCartResponse> =>
      await deleteRequest(`/account/customer/${customerId}/shoppingcartitem`),
    onSuccess: (response: ShoppingCartResponse) => {
      queryClient.setQueryData(["shoppingCart"], () => {
        return { shoppingCartList: [], price: response.price };
      });
    },
  });

  // handleUpdate handles the user input of increasing or decreasing the quantity of a specific item
  // it updates/deletes accordingly, depending on the
  const handleUpdate = async (
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
    } finally {
      setIsSubmitting(false);
    }
  };

  // handleDelete handles the user input of deleting a specific item
  const handleDelete = async (cartItem: ShoppingCartItemResponse) => {
    setIsSubmitting(true);
    try {
      deleteMutation.mutateAsync({ cartItemId: cartItem.id });
    } catch (err) {
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClear = async () => {
    setIsSubmitting(true);
    try {
      clearMutation.mutateAsync();
    } catch (err) {
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
    navigate({ to: "/cart/order" });
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
                  // find the corresponding clothing product for each shopping cart item
                  // required to retrieve certain information (price, image, etc.)
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
                              <p>Unit Price: {product.price}$</p>
                            </div>
                          </div>

                          {/* Right side of Card */}
                          <div className="flex flex-col items-center justify-between h-full">
                            {/* Quantity buttons */}
                            <div className="w-full justify-between grid grid-cols-[32px_40px_32px] items-center bg-default-100 border border-black rounded-full overflow-hidden text-center">
                              <Button
                                onPress={() =>
                                  handleUpdate(cartItem, cartItem.quantity - 1)
                                }
                                isDisabled={
                                  isSubmitting || cartItem.quantity <= 1
                                }
                                size="sm"
                                className="rounded-l-full border-r w-full"
                              >
                                -
                              </Button>
                              <div>{cartItem.quantity}</div>
                              <Button
                                onPress={() =>
                                  handleUpdate(cartItem, cartItem.quantity + 1)
                                }
                                isDisabled={isSubmitting}
                                size="sm"
                                className="rounded-r-full border-l w-full"
                              >
                                +
                              </Button>
                            </div>

                            {/* Delete button */}
                            <Button
                              onPress={() => handleDelete(cartItem)}
                              isDisabled={isSubmitting}
                              size="md"
                              className="rounded-full border-l bg-red-500 w-full"
                            >
                              <FaRegTrashAlt />
                            </Button>
                          </div>
                        </div>
                      ) : (
                        <div>
                          <Card.Header>No product found.</Card.Header>
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
