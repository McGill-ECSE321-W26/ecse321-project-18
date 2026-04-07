import { Link, createFileRoute } from "@tanstack/react-router";
import { Button, Card, Modal } from "@heroui/react";
import { useRef, useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { FaPlus, FaRegTrashAlt } from "react-icons/fa";
import { FaPencil } from "react-icons/fa6";
import { IoMdCheckmark } from "react-icons/io";
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
    <div className="-mt-10 flex flex-col gap-4">
      <Title pagename="Stilton's Store's Products" />
      <Button onPress={openCreateModal}>
        <FaPlus />
        Add product
      </Button>
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-4">
        {data.map(({ id, name, image }) => {
          return (
            <Card key={id} className="shadow-sm shadow-gray-400">
              <Card.Header className="flex justify-between items-center">
                <h3 className="font-bold text-lg">{name}</h3>
                <img
                  src={image && image !== "string" ? image : defaultImg}
                  alt={name}
                  className="my-3 h-48 object-cover rounded"
                />
                <div className="flex gap-3 items-center mt-2 w-full">
                  <Link
                    to="/admin/products/$productId"
                    params={{ productId: `${id}` }}
                    className="w-full"
                  >
                    <Button
                      size="sm"
                      isDisabled={deleteMutation.isPending}
                      className="w-full"
                    >
                      <FaPencil />
                      Edit
                    </Button>
                  </Link>
                  <Button
                    size="sm"
                    isDisabled={deleteMutation.isPending}
                    onPress={() => handleDeleteProduct(id)}
                    variant="danger"
                    className="w-full"
                  >
                    <FaRegTrashAlt />
                    Delete
                  </Button>
                </div>
              </Card.Header>
            </Card>
          );
        })}
      </div>

      <Modal.Backdrop isOpen={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <Modal.Container size="lg">
          <Modal.Dialog className="bg-gray-50">
            <Modal.CloseTrigger />
            <Modal.Header className="px-2 py-1">
              <Modal.Heading className="text-2xl font-bold">
                Add new product
              </Modal.Heading>
            </Modal.Header>
            <Modal.Body className="px-2 py-1">
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
                <IoMdCheckmark />
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
