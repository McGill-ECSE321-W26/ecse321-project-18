import { Link, createFileRoute } from "@tanstack/react-router";
import { Button, Card, Modal } from "@heroui/react";
import { HiOutlinePlusSm } from "react-icons/hi";
import { useRef, useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import type { ClothingProductRequest } from "#/types/api";

import {
  successToast,
  useClothingProducts,
  useCreateClothingProduct,
  useDeleteClothingProduct,
} from "#/utils/helpers";
import Skeleton from "#/components/Skeleton";
import { ProductForm } from "#/components/ProductForm";
import Title from "#/components/Title";

const queryClient = new QueryClient();
const defaultImg = "/stiltonslogo.png";

export const Route = createFileRoute("/_auth/admin/products/")({
  head: () => ({
    meta: [
      {
        title: "Manage products | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <AdminProducts />
    </QueryClientProvider>
  ),
});

function AdminProducts() {
  const { data, isLoading, error } = useClothingProducts();
  const deleteMutation = useDeleteClothingProduct();
  const createMutation = useCreateClothingProduct();

  const [isCreateOpen, setIsCreateOpen] = useState(false);
  const [name, setName] = useState("");
  const [price, setPrice] = useState("");
  const [image, setImage] = useState("");

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const resetCreateForm = () => {
    setName("");
    setPrice("");
    setImage("");
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const openCreateModal = () => {
    resetCreateForm();
    setIsCreateOpen(true);
  };

  const closeCreateModal = () => {
    setIsCreateOpen(false);
    resetCreateForm();
  };

  const handleCreateProduct = async () => {
    const product: ClothingProductRequest = {
      name: name.trim(),
      price: Number(Number(price).toFixed(2)),
      image: image.trim(),
    };
    try {
      await createMutation.mutateAsync(product);
      successToast("Successfully created product.");
    } catch (error) {
    } finally {
      closeCreateModal();
    }
  };

  const handleDeleteProduct = async (id: number) => {
    try {
      await deleteMutation.mutateAsync(id);
      successToast("Successfully deleted product.");
    } catch (error) {}
  };

  const handleImageSelect = async (e: { target: { files: any[] } }) => {
    const file = e.target.files[0];
    if (!file) return;

    const dataUrl = await fileToDataUrl(file);
    setImage(dataUrl);
  };

  if (isLoading) return <Skeleton />;
  if (error) return "Error: " + error.message;
  if (!data) {
    return (
      <div className="text-center text-red-600 font-semibold">
        Data for products could not be fetched.
      </div>
    );
  }

  return (
    <div className="-mt-12 flex flex-col gap-4">
      <Title pagename="Stilton's Store's Products" />
      <Button onPress={openCreateModal}>
        <HiOutlinePlusSm />
        Add Product
      </Button>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {data.map(({ id, name, image }) => {
          return (
            <Card key={id}>
              <Card.Header className="flex justify-between items-center">
                <span className="font-medium">{name}</span>
                <img
                  src={image && image !== "string" ? image : defaultImg}
                  alt={name}
                  className="w-48 h-48 object-cover rounded"
                  onError={(e) => {
                    e.currentTarget.src = defaultImg;
                  }}
                />
                <div className="flex gap-3 items-center">
                  <Link
                    to="/admin/products/$productId"
                    params={{ productId: `${id}` }}
                  >
                    <Button size="sm" isDisabled={deleteMutation.isPending}>
                      Edit
                    </Button>
                  </Link>
                  <Button
                    size="sm"
                    isDisabled={deleteMutation.isPending}
                    onPress={() => handleDeleteProduct(id)}
                    className="bg-red-600 text-white hover:bg-red-700"
                  >
                    Delete
                  </Button>
                </div>
              </Card.Header>
            </Card>
          );
        })}
      </div>

      <Modal.Backdrop isOpen={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <Modal.Container>
          <Modal.Dialog className="w-fit max-w-[95vw] overflow-visible">
            <Modal.CloseTrigger />
            <Modal.Header>
              <Modal.Heading>Add New Product</Modal.Heading>
            </Modal.Header>
            <Modal.Body className="overflow-visible">
              <ProductForm
                formId="create-product"
                name={name}
                setName={setName}
                price={price}
                setPrice={setPrice}
                image={image}
                fileInputRef={fileInputRef}
                onImageSelect={handleImageSelect}
                onSubmit={handleCreateProduct}
              />
            </Modal.Body>
            <Modal.Footer>
              <Button type="submit" form="create-product">
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
