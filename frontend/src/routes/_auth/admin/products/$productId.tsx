import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import {
  Button,
  FieldError,
  Form,
  Input,
  Label,
  Modal,
  TextField,
} from "@heroui/react";
import { createFileRoute } from "@tanstack/react-router";
import { useRef, useState } from "react";
import type {
  ClothingItemResponse,
  ClothingProductRequest,
  ClothingProductResponse,
} from "#/types/api";
import { ClothingColour, ClothingSize } from "#/types/api";
import { getRequest } from "#/utils/httpClient";
import {
  updateItemStock,
  useCreateClothingItem,
  useDeleteClothingItem,
  useUpdateClothingProduct,
} from "#/utils/helpers";
import { ProductForm } from "#/components/ProductForm";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/products/$productId")({
  loader: async ({ params }): Promise<ClothingProductResponse> => {
    return await getRequest<ClothingProductResponse>(
      `/clothingproduct/${params.productId}`,
    );
  },
  head: ({ loaderData }) => ({
    meta: [
      {
        title: `Manage ${loaderData?.name ?? "Product"} | Stilton's Store`,
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Product />
    </QueryClientProvider>
  ),
});

function useClothingProduct(id: number, initialData?: ClothingProductResponse) {
  return useQuery({
    queryKey: ["clothingProduct", id],
    queryFn: (): Promise<ClothingProductResponse> =>
      getRequest(`/clothingproduct/${id}`),
    initialData,
  });
}

function Product() {
  const { productId } = Route.useParams();
  const id = Number(productId);
  const [editedStock, setEditedStock] = useState<Record<number, number>>({});

  const [isCreateItemOpen, setIsCreateItemOpen] = useState(false);
  const [size, setSize] = useState<ClothingSize>(ClothingSize.M);
  const [colour, setColour] = useState<ClothingColour>(ClothingColour.BLACK);
  const [numInStock, setNumInStock] = useState("0");
  const [formError, setFormError] = useState("");

  const [isEditProductOpen, setIsEditProductOpen] = useState(false);
  const [productName, setProductName] = useState("");
  const [productPrice, setProductPrice] = useState("");
  const [productImage, setProductImage] = useState("");
  const [productFormError, setProductFormError] = useState("");

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const initialData = Route.useLoaderData();
  const { data } = useClothingProduct(id, initialData);
  const deleteItemMutation = useDeleteClothingItem(id);
  const createItemMutation = useCreateClothingItem(id);
  const updateProductMutation = useUpdateClothingProduct();

  const resetCreateForm = () => {
    setSize(ClothingSize.M);
    setColour(ClothingColour.BLACK);
    setNumInStock("0");
    setFormError("");
  };

  const openCreateForm = () => {
    resetCreateForm();
    setIsCreateItemOpen(true);
  };

  const closeCreateForm = () => {
    setIsCreateItemOpen(false);
    resetCreateForm();
  };

  const resetProductForm = () => {
    setProductName("");
    setProductPrice("");
    setProductImage("");
    setProductFormError("");

    fileInputRef.current?.value = "";
  };

  const openEditProductModal = () => {
    if (!data) return;

    setProductName(data.name);
    setProductPrice(String(data.price));
    setProductImage(data.image);
    setProductFormError("");
    setIsEditProductOpen(true);
  };

  const handleCreateItem = async () => {
    setFormError("");
    const parsedStock = Number(numInStock);

    if (!Number.isInteger(parsedStock) || parsedStock < 0) {
      return setFormError(
        "Stock must be a whole number greater than or equal to 0.",
      );
    }

    try {
      await createItemMutation.mutateAsync({
        size,
        colour,
        numInStock: parsedStock,
        clothingProductId: id,
      });
      closeCreateForm();
    } catch (error) {
      if (error instanceof Error) {
        setFormError(error.message);
      } else {
        setFormError("Could not create item.");
      }
    }
  };

  const handleUpdateProduct = async () => {
    setProductFormError("");

    if (!productName.trim()) return setFormError("Name is required.");
    if (!productImage.trim()) return setFormError("Image is required.");

    const parsedPrice = Number(productPrice);
    if ((parsedPrice * 100) % 1 != 0) {
      return setFormError("Price must have at most 2 decimal places.");
    }
    if (!Number.isFinite(parsedPrice) || parsedPrice < 0.01) {
      return setFormError("Price must be at least 0.01.");
    }

    const product: ClothingProductRequest = {
      name: productName.trim(),
      price: parsedPrice,
      image: productImage.trim(),
    };

    try {
      await updateProductMutation.mutateAsync({
        productId: id,
        product,
      });
      setIsEditProductOpen(false);
      resetProductForm();
    } catch (error) {
      if (error instanceof Error) {
        setProductFormError(error.message);
      } else {
        setProductFormError("Could not update product.");
      }
    }
  };

  const handleProductImageSelect = async (e: { target: { files: any[] } }) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      const dataUrl = await fileToDataUrl(file);
      setProductImage(dataUrl);
    } catch (error) {
      if (error instanceof Error) {
        setFormError(error.message);
      } else {
        setFormError("Could not read the selected image.");
      }
    }
  };

  if (!data) {
    return (
      <div className="text-center text-red-600 font-semibold">
        Data for product with ID {id} could not be fetched.
      </div>
    );
  }

  const items = data.clothingItems.sort((a, b) => a.id - b.id);

  async function handleUpdateStock(
    item: ClothingItemResponse,
    newStock: number,
  ) {
    await updateItemStock(id, item, newStock);
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center gap-3">
        <h2 className="text-2xl font-bold">{data.name}</h2>
        <Button
          className="bg-blue-600 text-white hover:bg-blue-700"
          onPress={openEditProductModal}
        >
          Update Product
        </Button>
      </div>

      <img
        src={
          data.image && data.image !== "string" ? data.image : "/IMG_4620.jpg"
        }
        alt={data.name}
        className="w-48 h-48 object-cover rounded"
        onError={(e) => {
          e.currentTarget.src = "/IMG_4620.jpg";
        }}
      />

      <p className="text-lg">Price: ${data.price}</p>

      <div className="mt-4 items-center flex gap-4">
        <h3 className="text-xl font-semibold">Items</h3>
        <Button
          onPress={() => {
            openCreateForm();
          }}
        >
          Add New Item
        </Button>
      </div>

      <ul className="space-y-3 pl-6">
        {items.map((item) => (
          <li key={item.id} className="flex items-center gap-4">
            <span>
              Size: {item.size} — Colour: {item.colour}
            </span>

            <Input
              type="number"
              defaultValue={item.numInStock}
              className="w-24"
              onChange={(e) =>
                setEditedStock((prev) => ({
                  ...prev,
                  [item.id]: Number(e.target.value),
                }))
              }
              aria-label="Stock quantity"
            />

            <Button
              size="sm"
              className="bg-blue-600 text-white hover:bg-blue-700"
              onPress={() =>
                handleUpdateStock(item, editedStock[item.id] ?? item.numInStock)
              }
            >
              Update
            </Button>

            <Button
              size="sm"
              isDisabled={deleteItemMutation.isPending}
              onPress={() => deleteItemMutation.mutate(item.id)}
              className="bg-red-600 text-white hover:bg-red-700"
            >
              {deleteItemMutation.isPending ? "Deleting..." : "Delete"}
            </Button>
          </li>
        ))}
      </ul>

      <Modal.Backdrop
        isOpen={isCreateItemOpen}
        onOpenChange={setIsCreateItemOpen}
      >
        <Modal.Container>
          <Modal.Dialog className="w-fit max-w-[95vw]">
            <Modal.CloseTrigger />
            <Modal.Header>
              <Modal.Heading>Add New Item</Modal.Heading>
            </Modal.Header>
            <Modal.Body>
              <Form
                className="flex w-full min-w-[18rem] max-w-[90vw] flex-col gap-4"
                onSubmit={handleCreateItem}
              >
                <div className="flex flex-col gap-2">
                  <Label>Size</Label>
                  <select
                    className="rounded-medium border border-default-200 bg-content1 px-3 py-2"
                    value={size}
                    onChange={(e) => setSize(e.target.value as ClothingSize)}
                  >
                    <option value="XS">XS</option>
                    <option value="S">S</option>
                    <option value="M">M</option>
                    <option value="L">L</option>
                    <option value="XL">XL</option>
                  </select>
                </div>

                <div className="flex flex-col gap-2">
                  <Label>Colour</Label>
                  <select
                    className="rounded-medium border border-default-200 bg-content1 px-3 py-2"
                    value={colour}
                    onChange={(e) =>
                      setColour(e.target.value as ClothingColour)
                    }
                  >
                    <option value="Red">Red</option>
                    <option value="Orange">Orange</option>
                    <option value="Yellow">Yellow</option>
                    <option value="Green">Green</option>
                    <option value="Blue">Blue</option>
                    <option value="Purple">Purple</option>
                    <option value="Pink">Pink</option>
                    <option value="Black">Black</option>
                    <option value="Grey">Grey</option>
                    <option value="White">White</option>
                    <option value="Brown">Brown</option>
                  </select>
                </div>

                <TextField
                  isRequired
                  name="numInStock"
                  type="number"
                  value={numInStock}
                  onChange={setNumInStock}
                >
                  <Label>Stock</Label>
                  <Input min={0} step={1} />
                  <FieldError />
                </TextField>
                {formError ? (
                  <p className="text-sm text-red-600">{formError}</p>
                ) : null}
              </Form>
            </Modal.Body>
            <Modal.Footer>
              <Button
                isDisabled={createItemMutation.isPending}
                onPress={handleCreateItem}
              >
                {createItemMutation.isPending ? "Creating..." : "Confirm"}
              </Button>
            </Modal.Footer>
          </Modal.Dialog>
        </Modal.Container>
      </Modal.Backdrop>

      <Modal.Backdrop
        isOpen={isEditProductOpen}
        onOpenChange={setIsEditProductOpen}
      >
        <Modal.Container>
          <Modal.Dialog className="w-fit max-w-[95vw]">
            <Modal.CloseTrigger />
            <Modal.Header>
              <Modal.Heading>Update Product</Modal.Heading>
            </Modal.Header>
            <Modal.Body>
              <ProductForm
                name={productName}
                setName={setProductName}
                price={productPrice}
                setPrice={setProductPrice}
                image={productImage}
                formError={productFormError}
                fileInputRef={fileInputRef}
                onImageSelect={handleProductImageSelect}
                onSubmit={handleUpdateProduct}
              />
            </Modal.Body>
            <Modal.Footer>
              <Button
                isDisabled={updateProductMutation.isPending}
                onPress={handleUpdateProduct}
              >
                {updateProductMutation.isPending ? "Updating..." : "Confirm"}
              </Button>
            </Modal.Footer>
          </Modal.Dialog>
        </Modal.Container>
      </Modal.Backdrop>
    </div>
  );
}

const fileToDataUrl = (file: File): Promise<string> =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => resolve(String(reader.result));
    reader.onerror = () => reject(new Error("Failed to read file"));
    reader.readAsDataURL(file);
  });
