import { Product } from "#/components/Product";

type ProductProps = {
  id: number;
  name: string;
  price: number;
  image?: string;
};

export default function ProductItem(props: ProductProps) {
  return (
    <>
      <div className="w-50">
        <Product
          id={props.id}
          name={props.name}
          price={props.price}
          image={props.image}
        />
      </div>
    </>
  );
}
