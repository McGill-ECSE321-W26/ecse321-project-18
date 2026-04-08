import {
  Button,
  FieldError,
  Form,
  Input,
  Label,
  TextField,
} from "@heroui/react";
import { useState } from "react";
import { FaRegTrashAlt } from "react-icons/fa";
import { FaClockRotateLeft } from "react-icons/fa6";
import type { ClothingItemResponse } from "#/types/api";
import {
  successToast,
  useDeleteClothingItem,
  useUpdateStock,
} from "#/utils/helpers";

export const EditClothingItem = ({
  clothingItem,
}: {
  clothingItem: ClothingItemResponse;
}) => {
  const { size, colour, id, numInStock, clothingProductId } = clothingItem;
  const [stock, setStock] = useState<string>(numInStock.toString());

  const deleteItemMutation = useDeleteClothingItem(clothingProductId);
  const updateStockMutation = useUpdateStock(clothingProductId);

  async function handleUpdateStock(
    item: ClothingItemResponse,
    newStock: number,
  ) {
    try {
      await updateStockMutation.mutateAsync({ item, newStock });
      successToast("Successfully updated item stock.");
    } catch (error) {}
  }

  async function handleDeleteItem(id: number) {
    try {
      await deleteItemMutation.mutateAsync(id);
      successToast("Successfully deleted item.");
    } catch (error) {}
  }

  return (
    <Form
      onSubmit={(e) => {
        e.preventDefault();
        handleUpdateStock(clothingItem, Number(stock));
      }}
    >
      <div className="p-5 bg-white rounded-3xl shadow-sm shadow-gray-400 hover:shadow-blue-400 hover:bg-blue-50 transition-all flex flex-col gap-4">
        <div className="flex justify-between items-center">
          <p className="font-semibold text-lg">
            {size} — {colour}
          </p>
        </div>

        <TextField
          className="flex flex-col gap-2"
          name="numInStock"
          isRequired
          type="number"
          value={stock}
          onChange={setStock}
        >
          <Label className="text-sm font-medium text-gray-600">Stock</Label>
          <Input min={0} step={1} className="w-full" />
          <FieldError />
        </TextField>

        <div className="flex gap-3 mt-auto">
          <Button size="sm" className="flex-1" type="submit">
            <FaClockRotateLeft />
            Update
          </Button>

          <Button
            size="sm"
            isDisabled={deleteItemMutation.isPending}
            onPress={() => handleDeleteItem(id)}
            className="flex-1"
            variant="danger"
          >
            <FaRegTrashAlt />
            Delete
          </Button>
        </div>
      </div>
    </Form>
  );
};
