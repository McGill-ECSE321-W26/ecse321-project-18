import { Card } from "@heroui/react";
import { Link } from "@tanstack/react-router";

type ProductProps = {
  id: number;
  name: string;
  price: number;
  image?: string;
};

export const Product = ({ id, name, price, image }: ProductProps) => {
  return (
    <Link
      className="h-max"
      to="/products/$productId"
      params={{ productId: id.toString() }}
    >
      <Card className="shadow-sm shadow-gray-400 hover:shadow-blue-400 hover:bg-blue-50 transition-all">
        <img
          className="rounded-md h-50 object-cover"
          src={image || "/stiltonslogo.png"}
          alt={name}
        />
        <Card.Header className="flex gap-1">
          <Card.Title className="text-lg font-bold">{name}</Card.Title>
          <Card.Description className="text-base text-black">
            {"$" + price}
          </Card.Description>
        </Card.Header>
      </Card>
    </Link>
  );
};
