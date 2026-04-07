import {
  Button,
  FieldError,
  Form,
  Input,
  Label,
  TextField,
} from "@heroui/react";
import { FaUpload } from "react-icons/fa6";
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
  return (
    <Form
      className="flex w-full flex-col gap-4"
      onSubmit={(e) => {
        e.preventDefault();
        void onSubmit();
      }}
      id={formId}
    >
      <TextField isRequired name="name" value={name} onChange={setName}>
        <Label className="text-base">Name</Label>
        <Input className="text-base" placeholder="Classic Hoodie" />
        <FieldError />
      </TextField>

      <TextField
        isRequired
        name="price"
        type="number"
        value={price}
        onChange={setPrice}
      >
        <Label className="text-base">Price</Label>
        <Input
          className="text-base"
          placeholder="Enter new price"
          min={0.01}
          step={0.01}
        />
        <FieldError />
      </TextField>

      <div className="flex flex-col gap-2">
        <Label className="text-base">Image</Label>
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
          className="text-base"
          onPress={() => fileInputRef.current?.click()}
        >
          <FaUpload />
          Choose image
        </Button>
        {image ? (
          <img
            src={image}
            alt="Selected preview"
            className="mt-2 h-48 rounded object-cover"
          />
        ) : null}
      </div>
    </Form>
  );
}
