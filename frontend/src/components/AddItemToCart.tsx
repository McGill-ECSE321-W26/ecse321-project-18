import { Card } from "@heroui/react";
import { useMutation } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import type {
  ClothingColour,
  ClothingItemResponse,
  ClothingSize,
  ShoppingCartItemRequest,
  ShoppingCartResponse,
} from "#/types/api";
import { successToast } from "#/utils/helpers";
import { postRequest } from "#/utils/httpClient";

type AddItemToCartProps = {
  price: number;
  colour: ClothingColour;
  items?: ClothingItemResponse[];
  isManager: boolean;
};

// POST to add cart endpoint
// const addMutation = useMutation({
//   mutationFn: async ({
//     shoppingCartItemRequest,
//   }: {
//     shoppingCartItemRequest: ShoppingCartItemRequest;
//   }): Promise<ShoppingCartResponse> =>
//     await postRequest(
//       `/account/customer/${customerId}/shoppingcartitem`,
//       shoppingCartItemRequest,
//     ),
//   onSuccess: () => {
//     successToast("Successfully added to cart!");
//     navigate({ to: "/cart" });
//   },
// });

// Handle adding to cart
// const handleAddtoCart = async (cartItem: ShoppingCartItemRequest) => {
//   try {
//     await addMutation.mutateAsync({ shoppingCartItemRequest: cartItem });
//   } catch (err) {}
// };

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

export default function AddItemToCart({
  price,
  colour,
  items,
  isManager,
}: AddItemToCartProps) {
  const areItemsAvailable = items && items.length > 0;
  const defaultSize = areItemsAvailable ? items[0].size : null;
  const defaultNumInStock = areItemsAvailable ? items[0].numInStock : 0;
  const [selectedSize, setSelectedSize] = useState<ClothingSize | null>(
    defaultSize,
  );
  const [selectedNumInStock, setSelectedNumInStock] =
    useState<number>(defaultNumInStock);
  const [quantity, setQuantity] = useState<number>(1);

  useEffect(() => {
    setSelectedSize(defaultSize);
    setSelectedNumInStock(defaultNumInStock);
  }, [colour, items]);

  return (
    <Card>
      <h3 className="text-xl">
        <strong>Price:</strong> ${price}
      </h3>
      <h3 className="text-xl">
        <strong>Size:</strong> {selectedSize || "no size selected."}
      </h3>
      {areItemsAvailable ? (
        <>
          <div className="grid grid-cols-5 items-center gap-4">
            {items.map((item) => {
              const { size, numInStock } = item;

              return (
                <p
                  key={size}
                  className={
                    (selectedSize === size
                      ? "font-bold border-2 border-blue-900 "
                      : "") +
                    "text-center p-4 bg-white shadow-sm shadow-gray-400 rounded-3xl hover:cursor-pointer hover:shadow-blue-400 hover:bg-blue-50 transition-all"
                  }
                  onClick={() => {
                    setSelectedSize(size);
                    setSelectedNumInStock(numInStock);
                  }}
                >
                  {size}
                </p>
              );
            })}
          </div>
          <h3 className="text-xl">
            {selectedNumInStock !== 0 ? (
              <>
                <strong>In stock:</strong> {selectedNumInStock} remaining
              </>
            ) : (
              <>
                <span className="font-bold text-red-500">Out of stock.</span>{" "}
                (You can still add this to your cart as a wishlist item.)
              </>
            )}
          </h3>
        </>
      ) : (
        <p>No items available.</p>
      )}
    </Card>
  );
}
