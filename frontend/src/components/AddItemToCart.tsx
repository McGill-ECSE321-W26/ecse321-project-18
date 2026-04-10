import { Button, Card, Label, NumberField, toast } from "@heroui/react";
import { useMutation } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate } from "@tanstack/react-router";
import { FaCartPlus } from "react-icons/fa6";
import type {
  ClothingColour,
  ClothingItemResponse,
  ClothingSize,
  ShoppingCartItemRequest,
  ShoppingCartResponse,
} from "#/types/api";
import { postRequest } from "#/utils/httpClient";
import { useAuth } from "#/auth";

type AddItemToCartProps = {
  price: number;
  colour: ClothingColour;
  items?: ClothingItemResponse[];
  isManager: boolean;
};

export default function AddItemToCart({
  price,
  colour,
  items,
  isManager,
}: AddItemToCartProps) {
  // Variables
  const areItemsAvailable = items && items.length > 0;
  const defaultSize = areItemsAvailable ? items[0].size : null;
  const defaultNumInStock = areItemsAvailable ? items[0].numInStock : 0;
  const [selectedSize, setSelectedSize] = useState<ClothingSize | null>(
    defaultSize,
  );
  const [selectedNumInStock, setSelectedNumInStock] =
    useState<number>(defaultNumInStock);
  const [quantity, setQuantity] = useState<number>(1);
  const customerId = useAuth().user?.id;
  const navigate = useNavigate(); // for navigating to cart after adding item

  useEffect(() => {
    setSelectedSize(defaultSize);
    setSelectedNumInStock(defaultNumInStock);
  }, [colour, items]);

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
      toast.success("Successfully added to cart!", {
        actionProps: {
          children: "Go to cart",
          onPress: () => navigate({ to: "/cart" }),
          className: "bg-success text-white",
        },
        description: "Continue shopping or place your order.",
        timeout: 10000,
      });
    },
  });

  // Takes in size and quantity and
  // returns a shoppingCartItemRequest to pass into handleAddToCart().
  const createShoppingCartItemRequest = (
    size: ClothingSize | null,
    qty: number,
  ) => {
    if (size === null) {
      const out: ShoppingCartItemRequest = { clothingItemId: -1, quantity: -1 };
      return out;
    }
    const clothingItem = items?.find((u) => u.size === size);
    let id = 0;
    clothingItem !== undefined ? (id = clothingItem.id) : (id = 0);
    const out: ShoppingCartItemRequest = { clothingItemId: id, quantity: qty };
    return out;
  };

  // Handle adding to cart
  const handleAddtoCart = async (cartItem: ShoppingCartItemRequest) => {
    try {
      await addMutation.mutateAsync({ shoppingCartItemRequest: cartItem });
    } catch (err) {}
  };

  return (
    <>
      <Card>
        <h3 className="text-xl">
          <strong>Price:</strong> ${price.toFixed(2)}
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
                        ? "font-bold border-2 border-[var(--color-button-1)] "
                        : "") +
                      "text-center p-4 bg-white shadow-sm shadow-gray-400 rounded-3xl hover:cursor-pointer hover:shadow-md hover:bg-blue-50 transition-all"
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
            {selectedNumInStock !== 0 ? (
              <>
                <h3 className="text-xl">
                  <strong>In stock:</strong> {selectedNumInStock} remaining
                </h3>
                <NumberField
                  className="w-full max-w-64 flex flex-row items-center gap-3"
                  defaultValue={1}
                  minValue={1}
                  step={1}
                  name="quantity"
                  maxValue={selectedNumInStock}
                  onChange={setQuantity}
                  isDisabled={isManager}
                >
                  <Label className="text-xl font-bold">Quantity:</Label>
                  <NumberField.Group className="shadow-sm shadow-gray-400">
                    <NumberField.DecrementButton />
                    <NumberField.Input className="text-center" />
                    <NumberField.IncrementButton />
                  </NumberField.Group>
                </NumberField>
              </>
            ) : (
              <h3 className="text-xl font-bold text-red-500">Out of stock.</h3>
            )}
          </>
        ) : (
          <p>No items available.</p>
        )}
      </Card>
      <Button
        onPress={() =>
          handleAddtoCart(createShoppingCartItemRequest(selectedSize, quantity))
        }
        isDisabled={selectedNumInStock <= 0 || isManager}
        className="mt-5 w-full text-base"
      >
        <FaCartPlus />
        {isManager ? "Cannot add as manager" : "Add to cart"}
      </Button>
    </>
  );
}
