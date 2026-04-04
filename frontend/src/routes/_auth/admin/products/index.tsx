import { Link, createFileRoute } from "@tanstack/react-router";
import { Button, Card, Modal } from "@heroui/react";
import { HiOutlinePlusSm } from "react-icons/hi";
import { useRef, useState } from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import type { ClothingProductRequest } from "#/types/api";

import {
  useClothingProducts,
  useCreateClothingProduct,
  useDeleteClothingProduct,
} from "#/utils/helpers";
import Skeleton from "#/components/Skeleton";
import { ProductForm } from "#/components/ProductForm";

const queryClient = new QueryClient();

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
  const [formError, setFormError] = useState("");

  const fileInputRef = useRef<HTMLInputElement | null>(null);

  const resetCreateForm = () => {
    setName("");
    setPrice("");
    setImage("");
    setFormError("");
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
    setFormError("");

    if (!name.trim()) return setFormError("Name is required.");
    if (!image.trim()) return setFormError("Image is required.");

    const parsedPrice = Number(price);
    if ((parsedPrice * 100) % 1 != 0) {
      return setFormError("Price must have at most 2 decimal places.");
    }
    if (!Number.isFinite(parsedPrice) || parsedPrice < 0.01) {
      return setFormError("Price must be at least 0.01.");
    }

    const product: ClothingProductRequest = {
      name: name.trim(),
      price: parsedPrice,
      image: image.trim(),
    };

    try {
      await createMutation.mutateAsync(product);
      closeCreateModal();
    } catch (err) {
      if (err instanceof Error) {
        setFormError(err.message);
      } else {
        setFormError("An unknown error occurred. Product could not be created");
      }
    }
  };

  const handleImageSelect = async (e: { target: { files: any[] } }) => {
    const file = e.target.files[0];
    if (!file) return;

    try {
      const dataUrl = await fileToDataUrl(file);
      setImage(dataUrl);
    } catch (err) {
      if (err instanceof Error) {
        setFormError(err.message);
      } else {
        setFormError("Could not read the selected image.");
      }
    }
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
    <div className="-mt-12">
      <h2 className="flex text-2xl font-bold items-center justify-center pt-4 mb-6">
        Stilton's Store's Products
      </h2>
      <Button onPress={openCreateModal}>
        <HiOutlinePlusSm />
        Add Product
      </Button>
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {data.map(({ id, name, image }) => (
          <Card key={id}>
            <Card.Header className="flex justify-between items-center">
              <span className="font-medium">{name}</span>
              <img
                src={image && image !== "string" ? image : "/IMG_4620.jpg"}
                alt={name}
                className="w-48 h-48 object-cover rounded"
                onError={(e) => {
                  e.currentTarget.src = "/IMG_4620.jpg";
                }}
              />
              <div className="flex gap-3 items-center">
                <Link
                  to="/admin/products/$productId"
                  params={{ productId: `${id}` }}
                  className="text-blue-600 underline"
                >
                  Edit
                </Link>
                <Button
                  size="sm"
                  isDisabled={deleteMutation.isPending}
                  onPress={() => deleteMutation.mutate(id)}
                  className="bg-red-600 text-white hover:bg-red-700"
                >
                  {deleteMutation.isPending ? "Deleting..." : "Delete"}
                </Button>
              </div>
            </Card.Header>
          </Card>
        ))}
      </div>

      <Modal.Backdrop isOpen={isCreateOpen} onOpenChange={setIsCreateOpen}>
        <Modal.Container>
          <Modal.Dialog className="w-fit max-w-[95vw]">
            <Modal.CloseTrigger />
            <Modal.Header>
              <Modal.Heading>Add New Product</Modal.Heading>
            </Modal.Header>
            <Modal.Body>
              <ProductForm
                name={name}
                setName={setName}
                price={price}
                setPrice={setPrice}
                image={image}
                formError={formError}
                fileInputRef={fileInputRef}
                onImageSelect={handleImageSelect}
                onSubmit={handleCreateProduct}
              />
            </Modal.Body>
            <Modal.Footer>
              <Button
                isDisabled={createMutation.isPending}
                onPress={handleCreateProduct}
              >
                {createMutation.isPending ? "Creating..." : "Confirm"}
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
