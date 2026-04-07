import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { Button, Table } from "@heroui/react";

import type { EmployeeResponse, OrderResponse } from "#/types/api";
import CustomSkeleton from "#/components/CustomSkeleton";
import { OrderState } from "#/types/api";
import { useAccounts, useClothingProducts, useOrders } from "#/utils/helpers";
import EmptyTable from "#/components/EmptyTable";
import Title from "#/components/Title";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/")({
  head: () => ({
    meta: [
      {
        title: "Dashboard | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <AdminDashboard />
    </QueryClientProvider>
  ),
});

function AdminDashboard() {
  const navigate = useNavigate();

  const { isLoading: isOrdersLoading, data: orders } = useOrders();

  const { isLoading: isAccountsLoading, data: accounts } = useAccounts();

  const { isLoading: isProductsLoading, data: products } =
    useClothingProducts();

  if (isOrdersLoading || isAccountsLoading || isProductsLoading) {
    return <CustomSkeleton />;
  }

  if (!orders || !accounts || !products) {
    return "An error has occurred: Server returned invalid data.";
  }

  const totalOrders = orders.length;
  const assignedOrders = orders.filter(
    (order: OrderResponse) => order.state === OrderState.ASSIGNED,
  ).length;
  const preparedOrders = orders.filter(
    (order: OrderResponse) => order.state === OrderState.PREPARED,
  ).length;
  const cancelledOrders = orders.filter(
    (order: OrderResponse) => order.state === OrderState.CANCELLED,
  ).length;
  const unassignedOrders = orders.filter(
    (order: OrderResponse) =>
      order.state === OrderState.PURCHASED && order.employeeId == null,
  ).length;

  const totalCustomers = accounts.customers.length;
  const totalEmployees = accounts.employees.length;
  const totalProducts = products.length;

  const recentOrders = [...orders]
    .sort((a: OrderResponse, b: OrderResponse) => b.id - a.id)
    .slice(0, 5);

  const employeeWorkload = accounts.employees
    .map((employee: EmployeeResponse) => {
      const assigned = orders.filter(
        (order: OrderResponse) =>
          order.employeeId === employee.id &&
          order.state === OrderState.ASSIGNED,
      ).length;

      const prepared = orders.filter(
        (order: OrderResponse) =>
          order.employeeId === employee.id &&
          order.state === OrderState.PREPARED,
      ).length;

      const total = orders.filter(
        (order: OrderResponse) => order.employeeId === employee.id,
      ).length;

      return {
        id: employee.id,
        email: employee.email,
        assigned,
        prepared,
        total,
      };
    })
    .sort((a, b) => b.total - a.total);

  const stockAlerts = products
    .flatMap((product) =>
      product.clothingItems.map((item) => ({
        productName: product.name,
        id: item.id,
        size: item.size,
        colour: item.colour,
        numInStock: item.numInStock,
      })),
    )
    .filter((item) => item.numInStock <= 5)
    .sort((a, b) => a.numInStock - b.numInStock)
    .slice(0, 5);

  const summaryCards = [
    {
      label: "Total orders",
      value: totalOrders,
      helper: "Orders submitted",
    },
    {
      label: "Unassigned orders",
      value: unassignedOrders,
      helper: "Purchased and waiting",
    },
    {
      label: "Assigned orders",
      value: assignedOrders,
      helper: "Assigned and being prepared",
    },
    {
      label: "Prepared orders",
      value: preparedOrders,
      helper: "Ready in workflow",
    },
    {
      label: "Customers",
      value: totalCustomers,
      helper: "Registered customers",
    },
    {
      label: "Employees",
      value: totalEmployees,
      helper: "Available employees",
    },
    {
      label: "Products",
      value: totalProducts,
      helper: "Products in the catalog",
    },
    {
      label: "Cancelled orders",
      value: cancelledOrders,
      helper: "Cancelled",
    },
  ];

  return (
    <div className="-mt-12 flex flex-col gap-4">
      <Title pagename="Dashboard" />

      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 xl:grid-cols-4">
        {summaryCards.map((card) => (
          <div
            key={card.label}
            className="rounded-2xl border border-default-200 bg-content1 p-5 shadow-sm"
          >
            <p className="text-sm text-default-500">{card.label}</p>
            <p className="mt-2 text-3xl font-semibold">{card.value}</p>
            <p className="mt-1 text-xs text-default-400">{card.helper}</p>
          </div>
        ))}
      </div>

      <div className="flex flex-wrap gap-3 justify-center">
        <Button onPress={() => navigate({ to: "/admin/orders" })}>
          Manage orders
        </Button>
        <Button onPress={() => navigate({ to: "/admin/accounts" })}>
          Manage accounts
        </Button>
        <Button onPress={() => navigate({ to: "/admin/products" })}>
          Manage products
        </Button>
        <Button onPress={() => navigate({ to: "/products" })}>View shop</Button>
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
        <div className="rounded-2xl border border-default-200 bg-content1 p-5 shadow-sm">
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h3 className="text-xl font-semibold">Recent orders</h3>
            </div>
            <Button
              variant="secondary"
              onPress={() => navigate({ to: "/admin/orders" })}
            >
              View all
            </Button>
          </div>

          <Table className="table-fixed w-full">
            <Table.ScrollContainer>
              <Table.Content aria-label="Recent orders">
                <Table.Header>
                  <Table.Column isRowHeader>ID</Table.Column>
                  <Table.Column>Status</Table.Column>
                  <Table.Column>Customer</Table.Column>
                  <Table.Column>Delivery</Table.Column>
                  <Table.Column>Price</Table.Column>
                </Table.Header>

                <EmptyTable
                  data={recentOrders}
                  message="No orders found"
                  renderRow={(order: OrderResponse) => (
                    <Table.Row key={order.id}>
                      <Table.Cell>{order.id}</Table.Cell>
                      <Table.Cell>{order.state}</Table.Cell>
                      <Table.Cell>{order.customerEmail}</Table.Cell>
                      <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                      <Table.Cell>{order.price}</Table.Cell>
                    </Table.Row>
                  )}
                />
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>

        <div className="rounded-2xl border border-default-200 bg-content1 p-5 shadow-sm">
          <div className="mb-4">
            <h3 className="text-xl font-semibold">Employee workload</h3>
          </div>

          <Table className="table-fixed w-full">
            <Table.ScrollContainer>
              <Table.Content aria-label="Employee workload">
                <Table.Header>
                  <Table.Column isRowHeader>Employee</Table.Column>
                  <Table.Column>Assigned</Table.Column>
                  <Table.Column>Prepared</Table.Column>
                  <Table.Column>Total handled</Table.Column>
                </Table.Header>

                <EmptyTable
                  data={employeeWorkload}
                  message="No employees found"
                  renderRow={(employee) => (
                    <Table.Row key={employee.id}>
                      <Table.Cell>{employee.email}</Table.Cell>
                      <Table.Cell>{employee.assigned}</Table.Cell>
                      <Table.Cell>{employee.prepared}</Table.Cell>
                      <Table.Cell>{employee.total}</Table.Cell>
                    </Table.Row>
                  )}
                />
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>
      </div>

      <div className="grid grid-cols-1 gap-6 xl:grid-cols-2">
        <div className="rounded-2xl border border-default-200 bg-content1 p-5 shadow-sm">
          <div className="mb-4">
            <h3 className="text-xl font-semibold">Unassigned orders</h3>
          </div>

          <Table className="table-fixed w-full">
            <Table.ScrollContainer>
              <Table.Content aria-label="Unassigned purchased orders">
                <Table.Header>
                  <Table.Column isRowHeader>ID</Table.Column>
                  <Table.Column>Customer</Table.Column>
                  <Table.Column>Delivery date</Table.Column>
                  <Table.Column>Price</Table.Column>
                </Table.Header>

                <EmptyTable
                  data={orders
                    .filter(
                      (order: OrderResponse) =>
                        order.state === OrderState.PURCHASED &&
                        order.employeeId == null,
                    )
                    .slice(0, 5)}
                  message="No unassigned purchased orders"
                  renderRow={(order: OrderResponse) => (
                    <Table.Row key={order.id}>
                      <Table.Cell>{order.id}</Table.Cell>
                      <Table.Cell>{order.customerEmail}</Table.Cell>
                      <Table.Cell>{order.deliveryDate.toString()}</Table.Cell>
                      <Table.Cell>{order.price}</Table.Cell>
                    </Table.Row>
                  )}
                />
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>

        <div className="rounded-2xl border border-default-200 bg-content1 p-5 shadow-sm">
          <div className="mb-4">
            <h2 className="text-xl font-semibold">Low stock items</h2>
          </div>

          <Table className="table-fixed w-full">
            <Table.ScrollContainer>
              <Table.Content aria-label="Low stock items">
                <Table.Header>
                  <Table.Column isRowHeader>Product</Table.Column>
                  <Table.Column>Size</Table.Column>
                  <Table.Column>Colour</Table.Column>
                  <Table.Column>Stock</Table.Column>
                </Table.Header>

                <EmptyTable
                  data={stockAlerts}
                  message="No low stock items"
                  renderRow={(item) => (
                    <Table.Row key={item.id}>
                      <Table.Cell>{item.productName}</Table.Cell>
                      <Table.Cell>{item.size}</Table.Cell>
                      <Table.Cell>{item.colour}</Table.Cell>
                      <Table.Cell>{item.numInStock}</Table.Cell>
                    </Table.Row>
                  )}
                />
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>
      </div>
    </div>
  );
}
