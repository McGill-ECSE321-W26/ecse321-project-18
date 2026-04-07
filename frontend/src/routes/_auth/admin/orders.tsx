import { Link, createFileRoute } from "@tanstack/react-router";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { Button, EmptyState, ListBox, Select, Table } from "@heroui/react";
import { Fragment, useState } from "react";

import { GoInbox } from "react-icons/go";
import { IoMdClose, IoMdEye, IoMdEyeOff, IoMdPersonAdd } from "react-icons/io";
import type { EmployeeResponse, OrderResponse } from "#/types/api";
import CustomSkeleton from "#/components/CustomSkeleton";
import { OrderItems } from "#/components/OrderItems";
import { OrderState } from "#/types/api";
import { successToast, useAccounts, useOrders } from "#/utils/helpers";
import { putRequest } from "#/utils/httpClient";
import { displayError } from "#/utils/error";
import Title from "#/components/Title";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/orders")({
  head: () => ({
    meta: [
      {
        title: "Manage orders | Stilton's Store",
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
  const [selectedEmployeeByOrder, setSelectedEmployeeByOrder] = useState<
    Record<number, string>
  >({});

  const {
    isLoading: isOrdersLoading,
    error: ordersError,
    data: data,
    refetch: refetchOrders,
  } = useOrders();

  const {
    isLoading: isAccountsLoading,
    error: accountsError,
    data: accounts,
  } = useAccounts();

  const updateStatusMutation = useMutation({
    mutationFn: async ({
      orderId,
      state,
      employeeId,
    }: {
      orderId: number;
      state: OrderState;
      employeeId: number | null;
    }) => {
      return putRequest(`/order/${orderId}/status`, {
        state,
        employeeId,
      });
    },
    onSuccess: async (_, variables) => {
      successToast(
        variables.state === OrderState.ASSIGNED
          ? "Employee assigned successfully."
          : "Order cancelled successfully.",
      );

      await refetchOrders();
    },
  });

  if (isOrdersLoading || isAccountsLoading) return <CustomSkeleton />;
  if (ordersError) return "An error has occurred: " + ordersError.message;
  if (accountsError) return "An error has occurred: " + accountsError.message;
  if (accounts === undefined || data === undefined) {
    return "An error has occurred: undefined values.";
  }

  const employees = accounts.employees;

  const toggleRow = (id: number) => {
    setExpandedRows((prev) =>
      prev.includes(id) ? prev.filter((rowId) => rowId !== id) : [...prev, id],
    );
  };

  const getSelectedEmployeeId = (order: OrderResponse) => {
    const selectedEmployee = selectedEmployeeByOrder[order.id];

    if (selectedEmployee) {
      return Number(selectedEmployee);
    }

    if (order.employeeId != null) {
      return order.employeeId;
    }

    return null;
  };

  const getEmployeeLabel = (employeeId: number | null) => {
    if (employeeId == null) {
      return "None";
    }

    const employee = employees.find(
      (entry: EmployeeResponse) => entry.id === employeeId,
    );

    return employee ? employee.email : String(employeeId);
  };

  const handleAssign = (order: OrderResponse) => {
    const employeeId = getSelectedEmployeeId(order);

    if (employeeId == null) {
      displayError("Must select an employee to assign.");
      return;
    }

    updateStatusMutation.mutate({
      orderId: order.id,
      state: OrderState.ASSIGNED,
      employeeId,
    });
  };

  const handleCancel = (order: OrderResponse) => {
    const employeeId = getSelectedEmployeeId(order);

    updateStatusMutation.mutate({
      orderId: order.id,
      state: OrderState.CANCELLED,
      employeeId,
    });
  };

  const orders = data.sort((a, b) => a.id - b.id);

  const availableOrders = orders.filter(
    (order: OrderResponse) => order.state === OrderState.PURCHASED,
  );

  const assignedOrders = orders.filter(
    (order: OrderResponse) => order.state === OrderState.ASSIGNED,
  );

  const completedOrders = orders.filter(
    (order: OrderResponse) =>
      order.state === OrderState.PREPARED ||
      order.state === OrderState.DELIVERED ||
      order.state === OrderState.CANCELLED,
  );

  const renderTable = (title: string, orders: OrderResponse[]) => (
    <div className="flex flex-col gap-4">
      <h2 className="text-xl font-semibold">{title}</h2>

      <Table className="table-fixed w-full">
        <Table.ScrollContainer>
          <Table.Content aria-label={title}>
            <Table.Header>
              <Table.Column isRowHeader>ID</Table.Column>
              <Table.Column>Status</Table.Column>
              <Table.Column>Customer email</Table.Column>
              <Table.Column>Total price</Table.Column>
              <Table.Column>Order date</Table.Column>
              <Table.Column>Delivery date</Table.Column>
              <Table.Column>Delivery address</Table.Column>
              <Table.Column>Assigned employee</Table.Column>
              <Table.Column>Choose employee</Table.Column>
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
                const selectedEmployeeId = getSelectedEmployeeId(order);

                return (
                  <Fragment key={order.id}>
                    <Table.Row>
                      <Table.Cell>{order.id}</Table.Cell>
                      <Table.Cell>{order.state}</Table.Cell>
                      <Table.Cell>{order.customerEmail}</Table.Cell>
                      <Table.Cell>${order.price.toFixed(2)}</Table.Cell>
                      <Table.Cell>{order.orderDate.toString()}</Table.Cell>
                      <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                      <Table.Cell>{order.deliveryAddress}</Table.Cell>

                      <Table.Cell>
                        {getEmployeeLabel(order.employeeId)}
                      </Table.Cell>

                      <Table.Cell>
                        <Select
                          placeholder="Select an employee"
                          value={selectedEmployeeId ?? ""}
                          aria-label="Employee selector"
                          isDisabled={order.state !== OrderState.PURCHASED}
                          onChange={(value) =>
                            setSelectedEmployeeByOrder(
                              (prev) =>
                                ({
                                  ...prev,
                                  [order.id]: value,
                                }) as Record<number, string>,
                            )
                          }
                        >
                          <Select.Trigger className="bg-gray-50">
                            <Select.Value />
                            <Select.Indicator />
                          </Select.Trigger>
                          <Select.Popover>
                            <ListBox className="w-full">
                              {employees.length === 0 ? (
                                <ListBox.Item textValue="" isDisabled>
                                  No employees available
                                </ListBox.Item>
                              ) : (
                                employees
                                  .filter(
                                    (employee: EmployeeResponse) =>
                                      employee.id !== order.customerId,
                                  )
                                  .map((employee: EmployeeResponse) => (
                                    <ListBox.Item
                                      key={employee.id}
                                      id={employee.id}
                                      textValue={employee.id.toString()}
                                    >
                                      {employee.email}
                                    </ListBox.Item>
                                  ))
                              )}
                            </ListBox>
                          </Select.Popover>
                        </Select>
                      </Table.Cell>

                      <Table.Cell>
                        <div className="flex gap-2">
                          <Button
                            onPress={() => handleAssign(order)}
                            isDisabled={
                              updateStatusMutation.isPending ||
                              order.state !== OrderState.PURCHASED ||
                              employees.length === 0
                            }
                          >
                            <IoMdPersonAdd />
                            Assign
                          </Button>

                          <Button
                            onPress={() => handleCancel(order)}
                            variant="danger"
                            isDisabled={
                              updateStatusMutation.isPending ||
                              order.state === OrderState.DELIVERED ||
                              order.state === OrderState.CANCELLED
                            }
                          >
                            <IoMdClose />
                            Cancel
                          </Button>
                        </div>
                      </Table.Cell>

                      <Table.Cell>
                        <Button
                          variant="secondary"
                          onPress={() => toggleRow(order.id)}
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

                    {isExpanded && <OrderItems order={order} colNum={11} />}
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
      <Title pagename="All Orders" />
      <nav className="sticky top-0 z-40 w-full border-b border-separator bg-background/70 backdrop-blur-lg">
        <header className="mx-auto flex h-16 max-w-5xl items-center justify-center px-6">
          <ul className="hidden items-center gap-4 md:flex">
            <li>
              <Link to="." href="/admin/orders#available">
                Available Orders
              </Link>
            </li>
            <li>
              <Link to="." href="/admin/orders#assigned">
                Assigned Orders
              </Link>
            </li>
            <li>
              <Link to="." href="/admin/orders#completed">
                Completed Orders
              </Link>
            </li>
          </ul>
        </header>
      </nav>

      <section id="available">
        {renderTable("Available Orders", availableOrders)}
      </section>
      <section id="assigned">
        {renderTable("Assigned Orders", assignedOrders)}
      </section>
      <section id="completed">
        {renderTable("Completed Orders", completedOrders)}
      </section>
    </div>
  );
}
