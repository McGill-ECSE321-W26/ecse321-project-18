import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { Button } from "@heroui/react";
import type {
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

  // Navigation and user tools
  const auth = useAuth();
  const customerId = auth.user?.id;
  const navigate = useNavigate();

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

  // find product image. if not fall back to default image
  // TODO: will need to relocate this after merging to main. -Qiuyu
  let imageLink = "/stiltonslogo.png";

  if (product.image) {
    imageLink = product.image;
  }

  // Handle adding to cart
  const handleAddtoCart = async (cartItem: ShoppingCartItemRequest) => {
    try {
      await addMutation.mutateAsync({ shoppingCartItemRequest: cartItem });
    } catch (err) {}
  };

  // dummy shopping cart item
  const dummy: ShoppingCartItemRequest = { clothingItemId: 1, quantity: 2 };

  return (
    <div className="pl-20">
      <div>
        <ProductItemComponent
          id={product.id}
          name={product.name}
          price={product.price}
          image={product.image}
        />
      </div>
      {!isManager && (
        <>
          <Button onPress={() => handleAddtoCart(dummy)}>Add to cart</Button>
        </>
      )}
    </div>
  );
}
