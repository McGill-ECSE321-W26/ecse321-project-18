import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { Button, EmptyState, Table } from "@heroui/react";
import { Fragment, useState } from "react";

import type { OrderResponse } from "#/types/api";
import { useAuth } from "#/auth";
import Skeleton from "#/components/Skeleton";
import { OrderItems } from "#/components/OrderItems";
import { AccountType, OrderState } from "#/types/api";
import { successToast, useOrders } from "#/utils/helpers";
import { putRequest } from "#/utils/httpClient";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/employee/")({
  beforeLoad: ({ context }) => {
    const userAccountType = context.auth.user?.accountType;

    if (userAccountType === AccountType.OWNER) {
      throw redirect({ to: "/admin/orders" });
    }

    if (userAccountType === AccountType.CUSTOMER) {
      throw redirect({ to: "/orders" });
    }
  },
  head: () => ({
    meta: [
      {
        title: "Manage orders | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <EmployeeOrders />
    </QueryClientProvider>
  ),
});

function EmployeeDashboard() {
  const auth = useAuth();
  const [expandedRows, setExpandedRows] = useState<number[]>([]);
  const [actionError, setActionError] = useState<string | null>(null);

  if (auth.user == null) {
    return "An error has occurred: User is not logged in";
  }

  const { isLoading, error, data, refetch } = useOrders();

  const updateStatusMutation = useMutation({
    mutationFn: async ({
      orderId,
      state,
    }: {
      orderId: number;
      state: OrderState;
    }) => {
      return putRequest(`/order/${orderId}/status`, {
        state,
        employeeId: auth.user!.id,
      });
    },
    onSuccess: async (_, variables) => {
      setActionError(null);

      successToast(
        variables.state === OrderState.ASSIGNED
          ? "Order assigned to you."
          : "Order marked as prepared.",
      );

      await refetch();
    },
    onError: (err: any) => {
      const backendErrors = err?.response?.data?.errors;

      if (backendErrors && Array.isArray(backendErrors)) {
        setActionError(backendErrors.join(", "));
      } else {
        setActionError("Something went wrong. Please try again.");
      }
    },
  });

  if (isLoading) return <Skeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  const toggleRow = (id: number) => {
    setExpandedRows((prev) =>
      prev.includes(id) ? prev.filter((rowId) => rowId !== id) : [...prev, id],
    );
  };

  const availableOrders = data.filter(
    (order: OrderResponse) =>
      order.state === OrderState.PURCHASED && order.employeeId == null,
  );

  const myOrders = data.filter(
    (order: OrderResponse) =>
      order.employeeId === auth.user!.id &&
      (order.state === OrderState.ASSIGNED ||
        order.state === OrderState.PREPARED),
  );

  const renderTable = (
    title: string,
    orders: OrderResponse[],
    mode: "available" | "mine",
  ) => {
    return (
      <div className="flex flex-col gap-4">
        <h2 className="text-xl font-semibold">{title}</h2>

        <Table className="table-fixed w-full">
          <Table.ScrollContainer>
            <Table.Content aria-label={title}>
              <Table.Header>
                <Table.Column isRowHeader>ID</Table.Column>
                <Table.Column>Status</Table.Column>
                <Table.Column>Customer email</Table.Column>
                <Table.Column>Order date</Table.Column>
                <Table.Column>Delivery date</Table.Column>
                <Table.Column>Delivery address</Table.Column>
                <Table.Column>Total price</Table.Column>
                <Table.Column>Actions</Table.Column>
                <Table.Column>Item details</Table.Column>
              </Table.Header>

              <Table.Body
                renderEmptyState={() => (
                  <EmptyState className="flex h-full w-full flex-col items-center justify-center gap-4 text-center">
                    <span className="text-sm text-muted">No orders found</span>
                  </EmptyState>
                )}
              >
                {orders.map((order: OrderResponse) => {
                  const isExpanded = expandedRows.includes(order.id);

                  return (
                    <Fragment key={order.id}>
                      <Table.Row>
                        <Table.Cell>{order.id}</Table.Cell>
                        <Table.Cell>{order.state}</Table.Cell>
                        <Table.Cell>{order.customerEmail}</Table.Cell>
                        <Table.Cell>{order.orderDate.toString()}</Table.Cell>
                        <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                        <Table.Cell>{order.deliveryAddress}</Table.Cell>
                        <Table.Cell>{order.price}</Table.Cell>

                        <Table.Cell>
                          <div className="flex gap-2">
                            {mode === "available" && (
                              <Button
                                onPress={() =>
                                  updateStatusMutation.mutate({
                                    orderId: order.id,
                                    state: OrderState.ASSIGNED,
                                  })
                                }
                                isDisabled={updateStatusMutation.isPending}
                              >
                                Self-assign
                              </Button>
                            )}

                            {mode === "mine" &&
                              order.state === OrderState.ASSIGNED && (
                                <Button
                                  onPress={() =>
                                    updateStatusMutation.mutate({
                                      orderId: order.id,
                                      state: OrderState.PREPARED,
                                    })
                                  }
                                  isDisabled={updateStatusMutation.isPending}
                                >
                                  Mark prepared
                                </Button>
                              )}
                          </div>
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
      </div>
    );
  };

  return (
    <div className="flex flex-col gap-8">
      <div>
        <h1 className="text-2xl font-semibold">Manage orders</h1>
        <p className="text-sm text-default-500">
          View available orders, assign them to yourself, and manage your own
          assigned orders.
        </p>
      </div>

      {actionError && <p className="text-sm text-danger">{actionError}</p>}

      {renderTable("Available orders", availableOrders, "available")}
      {renderTable("My assigned orders", myOrders, "mine")}
    </div>
  );
}
