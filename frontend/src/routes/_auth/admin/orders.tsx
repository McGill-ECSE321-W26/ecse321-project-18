import { createFileRoute } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { EmptyState, Table } from "@heroui/react";

import type { OrderResponse } from "#/types/api";
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

function Orders() {
  const { isLoading, error, data } = useOrders();

  if (isLoading) return <Skeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  return (
    <>
      <h2 className="text-xl">All Orders</h2>
      <Table>
        <Table.ScrollContainer>
          <Table.Content aria-label="Orders table">
            <Table.Header>
              <Table.Column isRowHeader>ID</Table.Column>
              <Table.Column>Status</Table.Column>
              <Table.Column>Price</Table.Column>
            </Table.Header>
            <Table.Body // this renders if the table contents are empty
              renderEmptyState={() => (
                <EmptyState className="flex h-full w-full flex-col items-center justify-center gap-4 text-center">
                  <span className="text-sm text-muted">No orders found</span>
                </EmptyState>
              )}
            >
              {data.map((order: OrderResponse) => {
                return (
                  <Table.Row key={order.id}>
                    <Table.Cell>{order.id}</Table.Cell>
                    <Table.Cell>{order.state}</Table.Cell>
                    <Table.Cell>{order.price}</Table.Cell>
                  </Table.Row>
                );
              })}
            </Table.Body>
          </Table.Content>
        </Table.ScrollContainer>
      </Table>
    </>
  );
}
