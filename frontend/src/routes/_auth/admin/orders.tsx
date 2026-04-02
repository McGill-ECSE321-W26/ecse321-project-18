import { createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Button, EmptyState, Table } from "@heroui/react";
import { Fragment, useState } from "react";
import type { OrderResponse } from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { useOrders } from "#/utils/helpers";
import { OrderItems } from "#/components/OrderItems";

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

function Orders() {
  const [expandedRows, setExpandedRows] = useState<number[]>([]);

  const { isLoading, error, data } = useOrders();

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
              <Table.Column>Status</Table.Column>
              <Table.Column>Customer email</Table.Column>
              <Table.Column>Total price</Table.Column>
              <Table.Column>Order date</Table.Column>
              <Table.Column>Delivery date</Table.Column>
              <Table.Column>Delivery address</Table.Column>
              <Table.Column>Assigned employee</Table.Column>
              <Table.Column>Item details</Table.Column>
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
                      <Table.Cell>{order.state}</Table.Cell>
                      <Table.Cell>{order.customerEmail}</Table.Cell>
                      <Table.Cell>{order.price}</Table.Cell>
                      <Table.Cell>{order.orderDate.toString()}</Table.Cell>
                      <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                      <Table.Cell>{order.deliveryAddress}</Table.Cell>
                      <Table.Cell>
                        {order.employeeId ? order.employeeId : "None"}
                      </Table.Cell>
                      <Table.Cell>
                        <Button onPress={() => toggleRow(order.id)}>
                          {isExpanded ? "Hide" : "Show"}
                        </Button>
                      </Table.Cell>
                    </Table.Row>
                    {isExpanded && <OrderItems order={order} colNum={9} />}
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
