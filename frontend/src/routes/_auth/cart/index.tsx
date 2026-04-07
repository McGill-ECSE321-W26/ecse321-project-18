import { Button, Card, Form, NumberField } from "@heroui/react";
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
  CustomerResponse,
  ShoppingCartItemRequest,
  ShoppingCartItemResponse,
  ShoppingCartListResponse,
  ShoppingCartResponse,
} from "#/types/api";
import { OrderModal } from "#/components/OrderModal";
import CustomSkeleton from "#/components/CustomSkeleton";
import Title from "#/components/Title";
import { deleteRequest, getRequest, putRequest } from "#/utils/httpClient";
import { useCart, useClothingProducts } from "#/utils/helpers";

const defaultImg = "/stiltonslogo.png";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/cart/")({
  loader: async ({ context }) => {
    const { user } = context.auth;

    // fetch number of loyalty points
    try {
      // user should not be null if they can stay on this page, but typechecker is strict
      // and doesn't hurt to be more careful anyway
      const res: CustomerResponse = await getRequest(
        `/account/customer/${user?.id}`,
      );
      return { customerId: user?.id, loyaltyPoints: res.numOfLoyaltyPoints };
    } catch (err) {
      return { customerId: null, loyaltyPoints: null };
    }
  },
  head: () => ({
    meta: [
      {
        title: "Cart | Stilton's Store",
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
  // const auth = useAuth();
  // const customerId = auth.user?.id;
  const { customerId, loyaltyPoints } = Route.useLoaderData();
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
    } catch {
    } finally {
      setIsSubmitting(false);
    }
  };

  // handleDelete handles the user input of deleting a specific item
  const handleDelete = async (cartItem: ShoppingCartItemResponse) => {
    setIsSubmitting(true);
    try {
      await deleteMutation.mutateAsync({ cartItemId: cartItem.id });
    } catch {
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleClear = async () => {
    setIsSubmitting(true);
    try {
      await clearMutation.mutateAsync();
    } catch {
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSubmit = async () => {
    setIsSubmitting(true);
  };

  if (isCartLoading || isProductsLoading) return <CustomSkeleton />;
  if (cartError) return "An error has occurred: " + cartError.message;
  if (productsError) return "An error has occurred: " + productsError.message;

  return (
    <>
      <div className="-mt-12 flex flex-col gap-4">
        <Title pagename="Your Cart" />
        <div>
          <Form
            action={handleSubmit}
            onReset={handleClear}
            className="grid lg:grid-cols-[3fr_1fr] gap-8 items-stretch"
          >
            {/* Left side */}
            <div className="grid grid-cols-1 gap-4 overflow-y-auto">
              {cartData?.shoppingCartList &&
              cartData.shoppingCartList.length != 0 ? (
                cartData.shoppingCartList.map(
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
                                src={product.image || defaultImg}
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
                                <p>Unit price: ${product.price.toFixed(2)}</p>
                              </div>
                            </div>

                            {/* Right side of Card */}
                            <div className="flex flex-col items-center justify-between h-full">
                              {/* Quantity buttons */}
                              <NumberField
                                variant="secondary"
                                name="quantity"
                                defaultValue={cartItem.quantity}
                                minValue={1}
                                step={1}
                                maxValue={cartItem.clothingItem.numInStock}
                                onChange={(newQuantity) =>
                                  handleUpdate(cartItem, newQuantity)
                                }
                                aria-label="Item quantity"
                              >
                                <NumberField.Group>
                                  <NumberField.DecrementButton />
                                  <NumberField.Input
                                    className="text-center text-base"
                                    onChange={(e) =>
                                      handleUpdate(
                                        cartItem,
                                        Number(e.target.value),
                                      )
                                    }
                                  />
                                  <NumberField.IncrementButton />
                                </NumberField.Group>
                              </NumberField>

                              {/* Delete button */}
                              <Button
                                onPress={() => handleDelete(cartItem)}
                                isDisabled={isSubmitting}
                                size="md"
                                variant="danger"
                                className="w-full"
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
                )
              ) : (
                <Card className="border-black border shadow-none text-center justify-center font-bold">
                  Your cart is currently empty.
                </Card>
              )}
            </div>

            {/* Right menu */}
            <div className="col-span-1 relative">
              <Card className="grid border border-black md:fixed md:w-[stretch] md:mr-6">
                <div className="grid content-start gap-2 md:text-lg">
                  <p>
                    <strong>Cart subtotal:</strong> $
                    {cartData ? cartData.price.toFixed(2) : 0}
                  </p>
                  <p>
                    <strong>
                      You currently have {loyaltyPoints} loyalty points.{" "}
                    </strong>
                    {loyaltyPoints < 100
                      ? `Earn ${100 - loyaltyPoints} more points to redeem a discount on a future order!`
                      : "You can choose to redeem 100 points for a discount during checkout!"}
                  </p>
                </div>
                <div className="grid sm:grid-cols-2 gap-2 border-t border-black pt-4 mt-auto">
                  <Button
                    type="reset"
                    isDisabled={
                      isSubmitting ||
                      !cartData ||
                      cartData.shoppingCartList.length == 0
                    }
                    className="w-full"
                    variant="danger"
                  >
                    <FaRegTrashAlt />
                    Clear
                  </Button>
                  <OrderModal
                    initialPrice={cartData ? cartData.price : 0}
                    isDisabled={
                      isSubmitting ||
                      !cartData ||
                      cartData.shoppingCartList.length == 0
                    }
                    navigate={navigate}
                    customerId={customerId}
                    setIsSubmitting={setIsSubmitting}
                  />
                </div>
              </Card>
            </div>
          </Form>
        </div>
      </div>
    </>
  );
}
