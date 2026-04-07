import { createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import {
  Button,
  Checkbox,
  CheckboxGroup,
  Form,
  Label,
  SearchField,
  Spinner,
} from "@heroui/react";

import { useState } from "react";
import { ClothingColour, ClothingSize } from "#/types/api";
import CustomSkeleton from "#/components/CustomSkeleton";
import { useMatchingClothingProducts } from "#/utils/helpers";
import { Product } from "#/components/Product";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/products/")({
  head: () => ({
    meta: [
      {
        title: "Products | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Products />
    </QueryClientProvider>
  ),
});

function Products() {
  // get all possible clothing sizes and colours as a list
  const CLOTHING_SIZES = Object.values(ClothingSize);
  const CLOTHING_COLOURS = Object.values(ClothingColour);

  const [searchName, setSearchName] = useState<string>("");
  // size and colour filter values will correspond to their enums, but
  // we can't impose that typing on the HeroUI component, so we type it as string[]
  const [sizeFilters, setSizeFilters] = useState<string[]>([]);
  const [colourFilters, setColourFilters] = useState<string[]>([]);
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const { isLoading, error, data, refetch } = useMatchingClothingProducts(
    searchName,
    sizeFilters,
    colourFilters,
  );

  const handleSubmit = async () => {
    setIsSubmitting(true);
    refetch();

    setIsSubmitting(false);
  };

  const handleReset = async () => {
    setIsSubmitting(true);

    setSearchName("");
    setSizeFilters([]);
    setColourFilters([]);

    refetch();
    setIsSubmitting(false);
  };

  if (isLoading) return <CustomSkeleton />;

  if (error) return "An error has occurred: " + error.message;

  return (
    <div className="grid md:grid-cols-[1fr_3fr] gap-5">
      <Form
        className="flex flex-col px-2 gap-3 sm:gap-5"
        onSubmit={(e) => {
          e.preventDefault();
          handleSubmit();
        }}
        onReset={handleReset}
      >
        {/* product search and filters */}
        <SearchField name="search" value={searchName} onChange={setSearchName}>
          <Label className="text-base">Search products</Label>
          <SearchField.Group>
            <SearchField.SearchIcon />
            <SearchField.Input placeholder="Enter a product name..." />
            <SearchField.ClearButton />
          </SearchField.Group>
        </SearchField>
        <hr />
        <CheckboxGroup
          name="size"
          value={sizeFilters}
          onChange={setSizeFilters}
        >
          <Label className="text-base">Filter by clothing size</Label>
          <div className="grid grid-cols-4 gap-x-1 lg:grid-cols-5">
            {CLOTHING_SIZES.map((size) => {
              return (
                // enums are encoded in uppercase on the backend, so requestparams need the enums in uppercase
                <Checkbox key={size} value={size.toUpperCase()}>
                  <Checkbox.Control>
                    <Checkbox.Indicator />
                  </Checkbox.Control>
                  <Checkbox.Content>
                    <Label>{size}</Label>
                  </Checkbox.Content>
                </Checkbox>
              );
            })}
          </div>
        </CheckboxGroup>
        <hr />
        <CheckboxGroup
          name="colour"
          value={colourFilters}
          onChange={setColourFilters}
        >
          <Label className="text-base">Filter by clothing colour</Label>
          <div className="grid grid-cols-2 gap-x-1 lg:grid-cols-3 xl:grid-cols-4">
            {CLOTHING_COLOURS.map((colour) => {
              return (
                // enums are encoded in uppercase on the backend, so requestparams need the enums in uppercase
                <Checkbox key={colour} value={colour.toUpperCase()}>
                  <Checkbox.Control>
                    <Checkbox.Indicator />
                  </Checkbox.Control>
                  <Checkbox.Content>
                    <Label>{colour}</Label>
                  </Checkbox.Content>
                </Checkbox>
              );
            })}
          </div>
        </CheckboxGroup>
        <hr />

        <Button
          className="w-full"
          type="submit"
          isDisabled={isSubmitting || data === undefined}
        >
          {isSubmitting ? (
            <Spinner size="sm" color="current" />
          ) : (
            "Apply search and filters"
          )}
        </Button>
        <Button
          variant="tertiary"
          className="w-full"
          type="reset"
          isDisabled={isSubmitting || data === undefined}
        >
          {isSubmitting ? <Spinner size="sm" /> : "Reset search and filters"}
        </Button>
      </Form>
      {/* display products (matching search and filters, if applicable) */}
      {data && data.length > 0 ? (
        <div className="grid gap-3 auto-rows-[1fr] sm:gap-5 md:gap-6 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 2xl:grid-cols-5">
          {data.map(({ name, price, image, id }) => {
            return (
              <Product
                key={id}
                id={id}
                name={name}
                price={price}
                image={image}
              />
            );
          })}
        </div>
      ) : (
        <p>No clothing products were found.</p>
      )}
    </div>
  );
}
