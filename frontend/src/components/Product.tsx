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
    <Card className="shadow-sm shadow-gray-400 hover:shadow-blue-400 hover:bg-blue-50 transition-all">
      <Link
        className="w-full h-full"
        to="/products/$productId"
        params={{ productId: id.toString() }}
      >
        <img src={image || "/stiltonslogo.png"} alt={name} />
        <Card.Header className="flex gap-1">
          <Card.Title className="text-lg font-bold">{name}</Card.Title>
          <Card.Description className="text-md text-black">
            {"$" + price}
          </Card.Description>
        </Card.Header>
      </Link>
    </Card>
  );
};
