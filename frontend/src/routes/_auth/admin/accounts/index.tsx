import { Link, createFileRoute } from "@tanstack/react-router";
import { Button, Table } from "@heroui/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import type {
  CustomerResponse,
  EmployeeResponse,
  OwnerResponse,
} from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { useAccounts } from "#/utils/helpers";
import EmptyTable from "#/components/EmptyTable";
import Title from "#/components/Title";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/accounts/")({
  head: () => ({
    meta: [
      {
        title: "Accounts | Stilton's Store",
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <Accounts />
    </QueryClientProvider>
  ),
});

function Accounts() {
  const navigate = Route.useNavigate();
  const { isLoading, error, data } = useAccounts();

  if (isLoading) return <Skeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  const handlePress = async (id: number) => {
    await navigate({ to: `/admin/accounts/${id}` });
  };

  return (
    <>
      <div className="-mt-12">
        <Title pagename="Stilton's Store's Accounts" />
        <nav className="sticky top-0 z-40 w-full border-b border-separator bg-background/70 backdrop-blur-lg">
          <header className="mx-auto flex h-16 max-w-5xl items-center justify-center px-6">
            <ul className="hidden items-center gap-4 md:flex">
              <li>
                <Link to="." href="/admin/accounts#owners">
                  Owners
                </Link>
              </li>
              <li>
                <Link to="." href="/admin/accounts#customers">
                  Customers
                </Link>
              </li>
              <li>
                <Link to="." href="/admin/accounts#employees">
                  Employees
                </Link>
              </li>
            </ul>
          </header>
        </nav>
        <div className="flex flex-col gap-12 py-8">
          <section id="owners">
            <h2 className="text-2xl font-bold mb-4">Owners</h2>
            <Table>
              <Table.ScrollContainer>
                <Table.Content aria-label="Owner table">
                  <Table.Header>
                    <Table.Column isRowHeader>ID</Table.Column>
                    <Table.Column>Email</Table.Column>
                    <Table.Column>Button</Table.Column>
                  </Table.Header>
                  <EmptyTable
                    data={data.owners}
                    renderRow={(owner) => (
                      <Table.Row key={owner.id}>
                        <Table.Cell>{owner.id}</Table.Cell>
                        <Table.Cell>{owner.email}</Table.Cell>
                        <Table.Cell>
                          <Button onPress={() => handlePress(owner.id)}>
                            View profile
                          </Button>
                        </Table.Cell>
                      </Table.Row>
                    )}
                  />
                </Table.Content>
              </Table.ScrollContainer>
            </Table>
          </section>

          <section id="customers">
            <h2 className="text-2xl font-bold mb-4">
              <a href="#customers">Customers</a>
            </h2>
            <Table>
              <Table.ScrollContainer>
                <Table.Content aria-label="Customer table">
                  <Table.Header>
                    <Table.Column isRowHeader>ID</Table.Column>
                    <Table.Column>Email</Table.Column>
                    <Table.Column>Address</Table.Column>
                    <Table.Column>Number of Purchased Orders</Table.Column>
                    <Table.Column>Loyalty Points</Table.Column>
                    <Table.Column>Button</Table.Column>
                  </Table.Header>
                  <EmptyTable
                    data={data.customers}
                    renderRow={(customer) => (
                      <Table.Row key={customer.id}>
                        <Table.Cell>{customer.id}</Table.Cell>
                        <Table.Cell>{customer.email}</Table.Cell>
                        <Table.Cell>{customer.address}</Table.Cell>
                        <Table.Cell>
                          {customer.purchasedOrders.length}
                        </Table.Cell>
                        <Table.Cell>{customer.numOfLoyaltyPoints}</Table.Cell>
                        <Table.Cell>
                          <Button onPress={() => handlePress(customer.id)}>
                            View profile
                          </Button>
                        </Table.Cell>
                      </Table.Row>
                    )}
                  />
                </Table.Content>
              </Table.ScrollContainer>
            </Table>
          </section>

          <section id="employees">
            <h2 className="text-2xl font-bold mb-4">
              <a href="#employees">Employees</a>
            </h2>
            <Table>
              <Table.ScrollContainer>
                <Table.Content aria-label="Employee table">
                  <Table.Header>
                    <Table.Column isRowHeader>ID</Table.Column>
                    <Table.Column>Email</Table.Column>
                    <Table.Column>Address</Table.Column>
                    <Table.Column>Loyalty Points</Table.Column>
                    <Table.Column>Number of Purchased Orders</Table.Column>
                    <Table.Column>Number of Assigned Orders</Table.Column>
                    <Table.Column>Button</Table.Column>
                  </Table.Header>
                  <EmptyTable
                    data={data.employees}
                    renderRow={(employee) => (
                      <Table.Row key={employee.id}>
                        <Table.Cell>{employee.id}</Table.Cell>
                        <Table.Cell>{employee.email}</Table.Cell>
                        <Table.Cell>{employee.address}</Table.Cell>
                        <Table.Cell>
                          {employee.purchasedOrders.length}
                        </Table.Cell>
                        <Table.Cell>
                          {employee.assignedOrders.length}
                        </Table.Cell>
                        <Table.Cell>{employee.numOfLoyaltyPoints}</Table.Cell>
                        <Table.Cell>
                          <Button onPress={() => handlePress(employee.id)}>
                            View profile
                          </Button>
                        </Table.Cell>
                      </Table.Row>
                    )}
                  />
                </Table.Content>
              </Table.ScrollContainer>
            </Table>
          </section>
        </div>
      </div>
    </>
  );
}
