import { createFileRoute } from "@tanstack/react-router";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
} from "@tanstack/react-query";
import { Button, EmptyState, Table } from "@heroui/react";
import { Fragment, useState } from "react";

import { GoInbox } from "react-icons/go";
import { IoMdClose, IoMdEye, IoMdEyeOff, IoMdPersonAdd } from "react-icons/io";
import type { EmployeeResponse, OrderResponse } from "#/types/api";
import Skeleton from "#/components/Skeleton";
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
  const [selectedEmployeeByOrder, setSelectedEmployeeByOrder] = useState<
    Record<number, string>
  >({});
  const [actionError, setActionError] = useState<string | null>(null);

  const {
    isLoading: isOrdersLoading,
    error: ordersError,
    data: orders,
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
      setActionError(null);

      successToast(
        variables.state === OrderState.ASSIGNED
          ? "Employee assigned successfully."
          : "Order cancelled successfully.",
      );

      await refetchOrders();
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

  if (isOrdersLoading || isAccountsLoading) return <Skeleton />;
  if (ordersError) return "An error has occurred: " + ordersError.message;
  if (accountsError) return "An error has occurred: " + accountsError.message;
  if (accounts === undefined || orders === undefined) {
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
      displayError("No employee can assigned.");
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

  return (
    <div className="-mt-12 flex flex-col gap-4">
      <Title pagename="All Orders" />

      {actionError && <p className="text-sm text-danger">{actionError}</p>}

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
                      <Table.Cell>${order.price}</Table.Cell>
                      <Table.Cell>{order.orderDate.toString()}</Table.Cell>
                      <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                      <Table.Cell>{order.deliveryAddress}</Table.Cell>

                      <Table.Cell>
                        {getEmployeeLabel(order.employeeId)}
                      </Table.Cell>

                      <Table.Cell>
                        <select
                          className="w-full rounded-md border border-default-300 bg-background px-2 py-1 text-sm"
                          value={selectedEmployeeId ?? ""}
                          onChange={(e) =>
                            setSelectedEmployeeByOrder((prev) => ({
                              ...prev,
                              [order.id]: e.target.value,
                            }))
                          }
                        >
                          <option value="" disabled>
                            Select an employee...
                          </option>
                          {employees.length === 0 ? (
                            <option value="" disabled>
                              No employees available
                            </option>
                          ) : (
                            employees
                              .filter(
                                (employee: EmployeeResponse) =>
                                  employee.id !== order.customerId,
                              )
                              .map((employee: EmployeeResponse) => (
                                <option key={employee.id} value={employee.id}>
                                  {employee.email}
                                </option>
                              ))
                          )}
                        </select>
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
                              Show
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
}
