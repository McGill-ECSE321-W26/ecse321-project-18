import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { Button, Checkbox, Form, Label, NumberField } from "@heroui/react";
import { useState } from "react";
import type {
  ClothingItemResponse,
  ClothingProductResponse,
  ShoppingCartItemRequest,
  ShoppingCartResponse,
} from "#/types/api";
import { AccountType } from "#/types/api";
import Skeleton from "#/components/Skeleton";
import ProductItemComponent from "#/components/ProductItem";
import { getRequest, postRequest } from "#/utils/httpClient";
import { useAuth } from "#/auth";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/products/$productId")({
  head: () => ({
    meta: [
      {
        title: "<Product> | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Product />
    </QueryClientProvider>
  ),
});

function useClothingProduct(id: number) {
  return useQuery({
    queryKey: ["clothingProduct"],
    queryFn: () => getRequest(`/clothingproduct/${id}`),
  });
}

function Product() {
  // Some inits
  const { productId }: { productId: number } = Route.useParams();

  // Navigation and user tools/hooks
  const auth = useAuth();
  const customerId = auth.user?.id;
  const navigate = useNavigate();
  const [value, setValue] = useState(0);

  // POST to add cart endpoint
  const addMutation = useMutation({
    mutationFn: async ({
      shoppingCartItemRequest,
    }: {
      shoppingCartItemRequest: ShoppingCartItemRequest;
    }): Promise<ShoppingCartResponse> =>
      await postRequest(
        `/account/customer/${customerId}/shoppingcartitem`,
        shoppingCartItemRequest,
      ),
    onSuccess: () => {
      navigate({ to: "/cart" });
    },
  });

  // Check if user is manager
  let isManager = false;
  if (auth.user?.accountType == AccountType.OWNER) {
    isManager = true;
  }

  // Fetch clothing product
  const {
    isLoading,
    error,
    data,
  }: { isLoading: boolean; error: Error | null; data: unknown } =
    useClothingProduct(productId);

  const product = data as ClothingProductResponse;

  if (isLoading) return <Skeleton />;
  if (error) return "An error has occurred: " + error.message;

  // Handle adding to cart
  const handleAddtoCart = async (cartItem: ShoppingCartItemRequest) => {
    try {
      await addMutation.mutateAsync({ shoppingCartItemRequest: cartItem });
    } catch (err) {}
  };

  // create ShoppingCartItemRequest from ClothingItemResponse
  const createShoppingCartItemRequest = (
    clothingItemResponse: ClothingItemResponse,
    qty: number,
  ) => {
    const sc: ShoppingCartItemRequest = {
      clothingItemId: clothingItemResponse.id,
      quantity: qty,
    };
    return sc;
  };

  // dummy shopping cart item for testing
  const dummy: ShoppingCartItemRequest = { clothingItemId: 1, quantity: 2 };

  return (
    <div className="pl-10">
      <div className="mb-10">
        <ProductItemComponent
          id={product.id}
          name={product.name}
          price={product.price}
          image={product.image}
        />
      </div>
      <div>
        {product.clothingItems.map((item) => (
          <>
            {item.numInStock > 0 && (
              <div className="p-5 mb-3 border-2">
                <p className="font-bold">ITEM</p>
                <div>Item Size: {item.size}</div>
                <div>Item Colour: {item.colour}</div>
                <div>Item Stock: {item.numInStock}</div>

                {!isManager && item.numInStock > 0 && (
                  <div className="pt-3">
                    <NumberField
                      className="w-full max-w-64"
                      defaultValue={1024}
                      minValue={0}
                      name="width"
                      value={value}
                      onChange={setValue}
                    >
                      <Label>Width</Label>
                      <NumberField.Group>
                        <NumberField.DecrementButton />
                        <NumberField.Input className="w-[120px]" />
                        <NumberField.IncrementButton />
                      </NumberField.Group>
                    </NumberField>
                    <Button
                      onPress={() =>
                        handleAddtoCart(
                          createShoppingCartItemRequest(item, value),
                        )
                      }
                    >
                      Add to cart
                    </Button>
                  </div>
                )}
              </div>
            )}
          </>
        ))}
      </div>
    </div>
  );
}
