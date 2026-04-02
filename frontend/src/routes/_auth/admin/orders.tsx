import { createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Button, EmptyState, Table } from "@heroui/react";

import { Fragment, useState } from "react";
import type { OrderItemResponse, OrderResponse } from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { useOrders } from "#/utils/helpers";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/orders")({
  head: () => ({
    meta: [
      {
        title: "Orders | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Orders />
    </QueryClientProvider>
  ),
});

interface OrderItemsProps {
  order: OrderResponse;
}

const OrderItems = ({ order }: OrderItemsProps) => {
  return (
    <Table.Row>
      <Table.Cell colSpan={6}>
        <Table>
          <Table.ScrollContainer>
            <Table.Content aria-label="Orders table">
              <Table.Header>
                <Table.Column isRowHeader>Name</Table.Column>
                <Table.Column>Size</Table.Column>
                <Table.Column>Colour</Table.Column>
                <Table.Column>Quantity</Table.Column>
                <Table.Column>Price</Table.Column>
              </Table.Header>
              <Table.Body>
                {order.orderItems.map((item: OrderItemResponse) => {
                  return (
                    <Table.Row key={item.id}>
                      <Table.Cell>
                        {item.clothingItem.clothingProductId}
                      </Table.Cell>
                      <Table.Cell>{item.clothingItem.colour}</Table.Cell>
                      <Table.Cell>{item.clothingItem.size}</Table.Cell>
                      <Table.Cell>{item.quantity}</Table.Cell>
                      <Table.Cell>{item.purchasePrice}</Table.Cell>
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

function Orders() {
  const { isLoading, error, data } = useOrders();
  const [expandedRows, setExpandedRows] = useState<number[]>([]);

  if (isLoading) return <Skeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  const toggleRow = (id: number) => {
    setExpandedRows((prev) =>
      prev.includes(id) ? prev.filter((rowId) => rowId !== id) : [...prev, id],
    );
  };

  return (
    <>
      <h2 className="text-xl">All Orders</h2>
      <Table className="table-fixed w-full">
        <Table.ScrollContainer>
          <Table.Content aria-label="Orders table">
            <Table.Header>
              <Table.Column isRowHeader>ID</Table.Column>
              <Table.Column>Customer email</Table.Column>
              <Table.Column>Status</Table.Column>
              <Table.Column>Assigned employee</Table.Column>
              <Table.Column>Price per unit</Table.Column>
              <Table.Column>Items</Table.Column>
            </Table.Header>
            <Table.Body // this renders if the table contents are empty
              renderEmptyState={() => (
                <EmptyState className="flex h-full w-full flex-col items-center justify-center gap-4 text-center">
                  <span className="text-sm text-muted">No orders found</span>
                </EmptyState>
              )}
            >
              {data.map((order: OrderResponse) => {
                const isExpanded = expandedRows.includes(order.id);
                return (
                  <Fragment key={order.id}>
                    <Table.Row>
                      <Table.Cell>{order.id}</Table.Cell>
                      <Table.Cell>{order.customerId}</Table.Cell>
                      <Table.Cell>{order.state}</Table.Cell>
                      <Table.Cell>
                        {order.employeeId ? order.employeeId : "None"}
                      </Table.Cell>
                      <Table.Cell>{order.price}</Table.Cell>
                      <Table.Cell>
                        <Button onPress={() => toggleRow(order.id)}>
                          {isExpanded ? "Hide" : "Show"}
                        </Button>
                      </Table.Cell>
                    </Table.Row>
                    {isExpanded && <OrderItems order={order} />}
                  </Fragment>
                );
              })}
            </Table.Body>
          </Table.Content>
        </Table.ScrollContainer>
      </Table>
    </>
  );
}
