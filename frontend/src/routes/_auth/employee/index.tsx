import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { Button, EmptyState, Spinner, Table } from "@heroui/react";
import { Fragment, useState } from "react";

import { GoInbox } from "react-icons/go";
import { IoMdEye, IoMdEyeOff, IoMdPersonAdd } from "react-icons/io";
import { MdAssignmentTurnedIn } from "react-icons/md";
import type { OrderResponse } from "#/types/api";
import { useAuth } from "#/auth";
import CustomSkeleton from "#/components/CustomSkeleton";
import { OrderItems } from "#/components/OrderItems";
import { AccountType, OrderState } from "#/types/api";
import { successToast, useOrders } from "#/utils/helpers";
import { putRequest } from "#/utils/httpClient";
import Title from "#/components/Title";

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
        title: "Manage Orders | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <EmployeeOrders />
    </QueryClientProvider>
  ),
});

function EmployeeOrders() {
  const auth = useAuth();
  const [expandedRows, setExpandedRows] = useState<number[]>([]);

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
      successToast(
        variables.state === OrderState.ASSIGNED
          ? "Order assigned to you."
          : "Order marked as prepared.",
      );

      await refetch();
    },
  });

  if (isLoading) return <CustomSkeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  const toggleRow = (id: number) => {
    setExpandedRows((prev) =>
      prev.includes(id) ? prev.filter((rowId) => rowId !== id) : [...prev, id],
    );
  };

  const availableOrders = data.filter(
    (order: OrderResponse) =>
      order.state === OrderState.PURCHASED &&
      order.employeeId == null &&
      order.customerId !== auth.user?.id,
  );

  const myOrders = data.filter(
    (order: OrderResponse) =>
      order.employeeId === auth.user!.id && order.state === OrderState.ASSIGNED,
  );

  const preparedOrders = data.filter(
    (order: OrderResponse) =>
      order.employeeId === auth.user!.id &&
      (order.state === OrderState.PREPARED ||
        order.state === OrderState.DELIVERED),
  );

  const renderTable = (
    title: string,
    orders: OrderResponse[],
    mode: "available" | "mine" | "prepared",
  ) => (
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
                  <GoInbox className="size-6 text-muted" />
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
                      <Table.Cell>${order.price.toFixed(2)}</Table.Cell>

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
                              {updateStatusMutation.isPending ? (
                                <Spinner size="sm" color="current" />
                              ) : (
                                <IoMdPersonAdd />
                              )}
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
                                {updateStatusMutation.isPending ? (
                                  <Spinner size="sm" color="current" />
                                ) : (
                                  <MdAssignmentTurnedIn />
                                )}
                                Mark prepared
                              </Button>
                            )}
                        </div>
                      </Table.Cell>

                      <Table.Cell>
                        <Button
                          onPress={() => toggleRow(order.id)}
                          variant="secondary"
                        >
                          {isExpanded ? (
                            <>
                              <IoMdEyeOff />
                              Hide
                            </>
                          ) : (
                            <>
                              <IoMdEye />
                              Expand
                            </>
                          )}
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

  return (
    <div className="-mt-12 flex flex-col gap-4">
      <Title pagename="Manage Orders" />

      {renderTable("Available Orders", availableOrders, "available")}
      {renderTable("My Assigned Orders", myOrders, "mine")}
      {renderTable("My Prepared Orders", preparedOrders, "prepared")}
    </div>
  );
}
