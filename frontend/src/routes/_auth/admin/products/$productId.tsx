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
  ListBox,
  Modal,
  Select,
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
  useCreateClothingItem,
  useDeleteClothingItem,
  useUpdateClothingProduct,
  useUpdateStock,
} from "#/utils/helpers";
import { ProductForm } from "#/components/ProductForm";

const queryClient = new QueryClient();
const defaultImg = "/stiltonslogo.png";

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
  const [size, setSize] = useState<ClothingSize | null>(null);
  const [colour, setColour] = useState<ClothingColour | null>(null);
  const [numInStock, setNumInStock] = useState("0");

  const [isEditProductOpen, setIsEditProductOpen] = useState(false);
  const [productName, setProductName] = useState("");
  const [productPrice, setProductPrice] = useState("");
  const [productImage, setProductImage] = useState("");

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const initialData = Route.useLoaderData();
  const { data } = useClothingProduct(id, initialData);
  const deleteItemMutation = useDeleteClothingItem(id);
  const createItemMutation = useCreateClothingItem(id);
  const updateProductMutation = useUpdateClothingProduct();
  const updateStockMutation = useUpdateStock(id);

  const resetCreateForm = () => {
    setSize(null);
    setColour(null);
    setNumInStock("0");
  };

  const openCreateForm = () => {
    resetCreateForm();
    setIsCreateItemOpen(true);
  };

  const closeCreateForm = () => {
    setIsCreateItemOpen(false);
    resetCreateForm();
  };

  const openEditProductModal = () => {
    if (!data) return;

    setProductName(data.name);
    setProductPrice(String(data.price));
    setProductImage(data.image);
    setIsEditProductOpen(true);
  };

  const handleCreateItem = async () => {
    const parsedStock = Number(numInStock);

    if (!size || !colour) return;

    await createItemMutation.mutateAsync({
      size,
      colour,
      numInStock: parsedStock,
      clothingProductId: id,
    });
    setEditedStock({});
    closeCreateForm();
  };

  const handleUpdateProduct = async () => {
    const product: ClothingProductRequest = {
      name: productName.trim(),
      price: Number(Number(productPrice).toFixed(2)),
      image: productImage.trim(),
    };

    await updateProductMutation.mutateAsync({
      productId: id,
      product,
    });
    setIsEditProductOpen(false);
  };

  const handleProductImageSelect = async (e: { target: { files: any[] } }) => {
    const file = e.target.files[0];
    if (!file) return;

    const dataUrl = await fileToDataUrl(file);
    setProductImage(dataUrl);
  };

  if (!data) {
    return (
      <div className="text-center text-red-600 font-semibold">
        Data for product with ID {id} could not be fetched.
      </div>
    );
  }

  const items = [...data.clothingItems].sort((a, b) => a.id - b.id);

  async function handleUpdateStock(
    item: ClothingItemResponse,
    newStock: number,
  ) {
    updateStockMutation.mutate({ item, newStock });
    setEditedStock({});
  }

  return (
    <div className="space-y-8">
      <div className="flex flex-col lg:flex-row gap-8 items-start">
        <img
          src={data.image && data.image !== "string" ? data.image : defaultImg}
          alt={data.name}
          className="w-64 h-64 object-cover rounded-3xl shadow-md shadow-gray-400"
          onError={(e) => {
            e.currentTarget.src = defaultImg;
          }}
        />

        <div className="flex flex-col gap-4">
          <h2 className="text-3xl font-bold">{data.name}</h2>
          <p className="text-xl font-semibold text-gray-700">
            Price: ${data.price}
          </p>

          <Button
            className="bg-blue-600 text-white hover:bg-blue-700 w-fit"
            onPress={openEditProductModal}
          >
            Edit Product
          </Button>
        </div>
      </div>

      <div className="flex items-center justify-between">
        <h3 className="text-2xl font-semibold">Items</h3>
        <Button
          onPress={openCreateForm}
          className="bg-green-600 text-white hover:bg-green-700"
        >
          Add New Item
        </Button>
      </div>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {items.map((item) => (
          <div
            key={item.id}
            className="p-5 bg-white rounded-3xl shadow-sm shadow-gray-400 hover:shadow-blue-400 hover:bg-blue-50 transition-all flex flex-col gap-4"
          >
            <div className="flex justify-between items-center">
              <p className="font-semibold text-lg">
                {item.size} — {item.colour}
              </p>
            </div>

            <div className="flex flex-col gap-2">
              <label className="text-sm font-medium text-gray-600">Stock</label>
              <Input
                type="number"
                value={editedStock[item.id] ?? item.numInStock}
                className="w-full"
                onChange={(e) =>
                  setEditedStock((prev) => ({
                    ...prev,
                    [item.id]: Number(e.target.value),
                  }))
                }
              />
            </div>

            <div className="flex gap-3 mt-auto">
              <Button
                size="sm"
                className="bg-blue-600 text-white hover:bg-blue-700 flex-1"
                onPress={() =>
                  handleUpdateStock(
                    item,
                    editedStock[item.id] ?? item.numInStock,
                  )
                }
              >
                Update
              </Button>

              <Button
                size="sm"
                isDisabled={deleteItemMutation.isPending}
                onPress={() => deleteItemMutation.mutate(item.id)}
                className="bg-red-600 text-white hover:bg-red-700 flex-1"
              >
                Delete
              </Button>
            </div>
          </div>
        ))}
      </div>

      <Modal.Backdrop
        isOpen={isCreateItemOpen}
        onOpenChange={setIsCreateItemOpen}
      >
        <Modal.Container>
          <Modal.Dialog className="w-fit max-w-[95vw] overflow-visible">
            <Modal.CloseTrigger />
            <Modal.Header>
              <Modal.Heading>Add New Item</Modal.Heading>
            </Modal.Header>
            <Modal.Body className="overflow-visible">
              <Form
                className="flex w-full min-w-[18rem] max-w-[90vw] flex-col gap-4"
                onSubmit={(e) => {
                  e.preventDefault();
                  void handleCreateItem();
                }}
                id="create-item"
              >
                <div className="flex flex-col gap-2">
                  <Select
                    isRequired
                    className="w-[256px]"
                    placeholder="Select size"
                    onChange={(key) => setSize(key as ClothingSize)}
                  >
                    <Label>Size</Label>
                    <Select.Trigger>
                      <Select.Value />
                      <Select.Indicator />
                    </Select.Trigger>
                    <Select.Popover>
                      <ListBox>
                        {Object.values(ClothingSize).map((value) => (
                          <ListBox.Item
                            key={value}
                            id={value}
                            textValue={value}
                          >
                            {value}
                            <ListBox.ItemIndicator />
                          </ListBox.Item>
                        ))}
                      </ListBox>
                    </Select.Popover>
                  </Select>
                </div>

                <div className="flex flex-col gap-2">
                  <Select
                    className="w-[256px]"
                    placeholder="Select colour"
                    isRequired
                    onChange={(key) => setColour(key as ClothingColour)}
                  >
                    <Label>Colour</Label>
                    <Select.Trigger>
                      <Select.Value />
                      <Select.Indicator />
                    </Select.Trigger>
                    <Select.Popover>
                      <ListBox>
                        {Object.values(ClothingColour).map((value) => (
                          <ListBox.Item
                            key={value}
                            id={value}
                            textValue={value}
                          >
                            {value}
                            <ListBox.ItemIndicator />
                          </ListBox.Item>
                        ))}
                      </ListBox>
                    </Select.Popover>
                  </Select>
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
              </Form>
            </Modal.Body>
            <Modal.Footer>
              <Button type="submit" form="create-item">
                Confirm
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
          <Modal.Dialog className="w-fit max-w-[95vw] overflow-visible">
            <Modal.CloseTrigger />
            <Modal.Header>
              <Modal.Heading>Edit Product</Modal.Heading>
            </Modal.Header>
            <Modal.Body className="overflow-visible">
              <ProductForm
                formId={"edit-product-form"}
                name={productName}
                setName={setProductName}
                price={productPrice}
                setPrice={setProductPrice}
                image={productImage}
                fileInputRef={fileInputRef}
                onImageSelect={handleProductImageSelect}
                onSubmit={handleUpdateProduct}
              />
            </Modal.Body>
            <Modal.Footer>
              <Button type="submit" form="edit-product-form">
                Confirm
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
