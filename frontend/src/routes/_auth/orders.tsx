import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createFileRoute, redirect } from "@tanstack/react-router";
import { Button, EmptyState, Table } from "@heroui/react";
import { Fragment, useState } from "react";
import { GoInbox } from "react-icons/go";
import { IoMdEye, IoMdEyeOff } from "react-icons/io";
import type { OrderResponse } from "#/types/api";
import { useAuth } from "#/auth";
import CustomSkeleton from "#/components/CustomSkeleton";
import { AccountType } from "#/types/api";
import { useCustomerOrders } from "#/utils/helpers";
import { OrderItems } from "#/components/OrderItems";
import Title from "#/components/Title";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/orders")({
  beforeLoad: ({ context }) => {
    const userAccountType = context.auth.user?.accountType;

    if (userAccountType === AccountType.OWNER) {
      throw redirect({
        to: "/admin/orders",
      });
    }
  },
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
  const auth = useAuth();

  if (auth.user == null) {
    return "An error has occurred: User is not logged in";
  }

  const { isLoading, error, data } = useCustomerOrders(auth.user.id);

  if (isLoading) return <CustomSkeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  const toggleRow = (id: number) => {
    setExpandedRows((prev) =>
      prev.includes(id) ? prev.filter((rowId) => rowId !== id) : [...prev, id],
    );
  };

  return (
    <>
      <div className="-mt-12 flex flex-col gap-4">
        <Title pagename="My Order History" />
        <Table className="table-fixed w-full table-variant-4">
          <Table.ScrollContainer>
            <Table.Content aria-label="Orders table">
              <Table.Header>
                <Table.Column isRowHeader>ID</Table.Column>
                <Table.Column>Status</Table.Column>
                <Table.Column>Total price</Table.Column>
                <Table.Column>Order date</Table.Column>
                <Table.Column>Delivery date</Table.Column>
                <Table.Column>Delivery address</Table.Column>
                <Table.Column>Item details</Table.Column>
              </Table.Header>
              <Table.Body // this renders if the table contents are empty
                renderEmptyState={() => (
                  <EmptyState className="flex table-empty-4 h-full w-full flex-col items-center justify-center gap-4 text-center">
                    <GoInbox className="size-6 " />
                    <span className="text-sm ">No orders found</span>
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
                        <Table.Cell>${order.price.toFixed(2)}</Table.Cell>
                        <Table.Cell>{order.orderDate.toString()}</Table.Cell>
                        <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                        <Table.Cell>{order.deliveryAddress}</Table.Cell>
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
                      {isExpanded && <OrderItems order={order} colNum={7} />}
                    </Fragment>
                  );
                })}
              </Table.Body>
            </Table.Content>
          </Table.ScrollContainer>
        </Table>
      </div>
    </>
  );
}
