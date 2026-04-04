import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import { ColorSwatch } from "@heroui/react";
import { useState } from "react";
import type { ClothingColour, ClothingProductResponse } from "#/types/api";
import { AccountType, ClothingColourHexes } from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";
import { useAuth } from "#/auth";
import AddItemToCart from "#/components/AddItemToCart";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/products/$productId")({
  loader: async ({ params }): Promise<ClothingProductResponse | null> => {
    try {
      return await getRequest<ClothingProductResponse>(
        `/clothingproduct/${params.productId}`,
        false,
      );
    } catch (err) {
      return null;
    }
  },
  head: ({ loaderData }) => ({
    meta: [
      {
        title: `${loaderData?.name || "Product"} | Stilton's Store`,
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
    queryFn: (): Promise<ClothingProductResponse> =>
      getRequest(`/clothingproduct/${id}`, true),
    retry: false,
  });
}

function Product() {
  // Some inits
  const CLOTHING_COLOURS = Object.fromEntries(
    Object.entries(ClothingColourHexes),
  ); // get colours as a key-value map: key = colour name, value = colour hex
  const { productId }: { productId: number } = Route.useParams();
  const initialProductData = Route.useLoaderData();

  // Navigation and user tools/hooks
  const auth = useAuth();

  const initialColour = initialProductData?.clothingItems[0].colour || null;
  const [selectedColour, setSelectedColour] = useState<ClothingColour | null>(
    initialColour,
  );

  // Check if user is manager
  const isManager = auth.user?.accountType == AccountType.OWNER;

  // Fetch clothing product
  const { isLoading, error, data } = useClothingProduct(productId);

  if (isLoading) return <Skeleton />;
  if (error)
    return <p>We couldn't find or load a product with ID {productId}!</p>;

  // group clothing items by colour
  const product = data as ClothingProductResponse;
  const itemsByColour = Object.groupBy(
    product.clothingItems,
    ({ colour }) => colour,
  );

  // set initial/default selected clothing colour if none chosen by user (e.g. on initial page load)
  if (!selectedColour) {
    setSelectedColour(Object.keys(itemsByColour)[0] as ClothingColour);
  }

  return (
    <>
      {data ? (
        <>
          <h2 className="text-2xl font-bold lg:hidden">{product.name}</h2>
          <div className="w-full grid gap-5 justify-center lg:grid-cols-2 xl:grid-cols-3">
            <img
              src={product.image || "/stiltonslogo.png"}
              alt={product.name}
            />
            <div className="grid gap-4 grid-cols-2 md:grid-cols-3 lg:flex lg:flex-col">
              <h2 className="text-2xl font-bold hidden lg:block">
                {product.name}
              </h2>

              {Object.entries(itemsByColour).map(([colour]) => {
                const colourHex = CLOTHING_COLOURS[colour.toUpperCase()];

                return (
                  <div
                    key={colour}
                    className={
                      (selectedColour === colour
                        ? "font-bold border-2 border-blue-900 "
                        : "") +
                      "flex items-center gap-2 p-4 bg-white shadow-sm shadow-gray-400 rounded-3xl hover:cursor-pointer hover:shadow-blue-400 hover:bg-blue-50 transition-all"
                    }
                    onClick={() => setSelectedColour(colour as ClothingColour)}
                  >
                    <ColorSwatch color={colourHex} size="xs" />
                    <p>{colour}</p>
                  </div>
                );
              })}
            </div>
            <div className="hidden xl:block">
              <AddItemToCart
                price={product.price}
                colour={selectedColour as ClothingColour}
                items={itemsByColour[selectedColour as ClothingColour]}
                isManager={isManager}
              />
            </div>
          </div>
          <div className="pt-5 xl:hidden">
            <AddItemToCart
              price={product.price}
              colour={selectedColour as ClothingColour}
              items={itemsByColour[selectedColour as ClothingColour]}
              isManager={isManager}
            />
          </div>
        </>
      ) : (
        <p>No product with ID {productId} found!</p>
      )}
    </>
  );
}
