import {
  Button,
  FieldError,
  Form,
  Input,
  Label,
  TextField,
} from "@heroui/react";
import type { RefObject } from "react";

type ProductFormProps = {
  formId: string;
  name: string;
  setName: (value: string) => void;
  price: string;
  setPrice: (value: string) => void;
  image: string;
  fileInputRef: RefObject<HTMLInputElement | null>;
  onImageSelect: (e: any) => void;
  onSubmit: () => void;
};

export function ProductForm({
  formId,
  name,
  setName,
  price,
  setPrice,
  image,
  fileInputRef,
  onImageSelect,
  onSubmit,
}: ProductFormProps) {
  const handlePriceBlur = () => {
    if (!price) return;
    setPrice(Number(price).toFixed(2));
  };

  return (
    <Form
      className="flex w-full min-w-[18rem] max-w-[90vw] flex-col gap-4"
      onSubmit={onSubmit}
      id={formId}
    >
      <TextField isRequired name="name" value={name} onChange={setName}>
        <Label>Name</Label>
        <Input placeholder="Classic Hoodie" />
        <FieldError />
      </TextField>

      <TextField
        isRequired
        name="price"
        type="number"
        value={price}
        onChange={setPrice}
      >
        <Label>Price</Label>
        <Input
          placeholder="49.99"
          min={0.01}
          step="any"
          onBlur={handlePriceBlur}
        />
        <FieldError />
      </TextField>

      <Label>Image</Label>
      <input
        ref={fileInputRef}
        type="file"
        accept="image/*"
        className="hidden"
        onChange={onImageSelect}
      />
      <Button
        type="button"
        variant="secondary"
        onPress={() => fileInputRef.current?.click()}
      >
        Choose Image
      </Button>
      {image ? (
        <img
          src={image}
          alt="Selected preview"
          className="mt-2 h-40 w-40 rounded object-cover"
        />
      ) : null}
    </Form>
  );
}
