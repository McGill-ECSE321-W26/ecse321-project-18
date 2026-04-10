import { Table } from "@heroui/react";
import type { OrderItemResponse, OrderResponse } from "#/types/api";

interface OrderItemsProps {
  order: OrderResponse;
  colNum: number;
  tableType?: string;
}

export const OrderItems = ({ order, colNum, tableType }: OrderItemsProps) => {
  return (
    <Table.Row>
      <Table.Cell colSpan={colNum}>
        <Table variant="secondary" className={`table-variant-${tableType}`}>
          <Table.ScrollContainer>
            <Table.Content aria-label="Orders table">
              <Table.Header>
                <Table.Column isRowHeader>Product name</Table.Column>
                <Table.Column>Size</Table.Column>
                <Table.Column>Colour</Table.Column>
                <Table.Column>Quantity</Table.Column>
                <Table.Column>Price per unit</Table.Column>
              </Table.Header>
              <Table.Body>
                {order.orderItems.map((item: OrderItemResponse) => {
                  return (
                    <Table.Row key={item.id}>
                      <Table.Cell>{item.productName}</Table.Cell>
                      <Table.Cell>{item.clothingItem.size}</Table.Cell>
                      <Table.Cell>{item.clothingItem.colour}</Table.Cell>
                      <Table.Cell>{item.quantity}</Table.Cell>
                      <Table.Cell>${item.purchasePrice.toFixed(2)}</Table.Cell>
                    </Table.Row>
                  );
                })}
              </Table.Body>
            </Table.Content>
          </Table.ScrollContainer>
        </Table>
      </Table.Cell>
    </Table.Row>
  );
};
