import { Link, createFileRoute } from "@tanstack/react-router";
import { Button, Table } from "@heroui/react";
import {
  QueryClient,
  QueryClientProvider,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";
import { FaArrowUpRightFromSquare } from "react-icons/fa6";
import CustomSkeleton from "#/components/CustomSkeleton";
import { successToast, useAccounts } from "#/utils/helpers";
import EmptyTable from "#/components/EmptyTable";
import Title from "#/components/Title";
import { deleteRequest } from "#/utils/httpClient";

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

  const queryClient = useQueryClient();

  const handleView = async (id: number) => {
    await navigate({ to: `/admin/accounts/${id}` });
  };

  const deleteAccountMutation = useMutation({
    mutationFn: (id: number) => deleteRequest<void>(`/account/${id}`),
    onSuccess: () => {
      successToast("Successfully deleted account.");
      queryClient.invalidateQueries({ queryKey: ["accounts"] });
    },
  });

  const handleDelete = async (id: number) => {
    try {
      await deleteAccountMutation.mutateAsync(id);
    } catch (error) {}
  };

  if (isLoading) return <CustomSkeleton />;
  if (error) return "An error has occurred: " + error.message;
  if (!data) return "An error has occurred: Server returned invalid data.";

  return (
    <>
      <div className="-mt-12">
        <div className="-mx-6 -mt-14 bg-[var(--color-header-2)]">
          <Title
            pagename={
              <>
                <span className="italic">Stilton</span>'s Store's Accounts
              </>
            }
            className="text-[var(--color-header-text-2)]"
          />
        </div>
        <nav className="w-screen -mx-6 sticky !bg-[var(--color-header-2)] top-0 z-40 border-b border-separator bg-background/70 backdrop-blur-lg">
          <header className="font-serif mx-auto flex h-16 max-w-5xl items-center justify-center text-[var(--color-header-text-2)] px-6">
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
                    <Table.Column>View Profile</Table.Column>
                  </Table.Header>
                  <EmptyTable
                    data={data.owners}
                    renderRow={(owner) => (
                      <Table.Row key={owner.id}>
                        <Table.Cell>{owner.id}</Table.Cell>
                        <Table.Cell>{owner.email}</Table.Cell>
                        <Table.Cell>
                          <Button onPress={() => handleView(owner.id)}>
                            <FaArrowUpRightFromSquare />
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
                    <Table.Column></Table.Column>
                    <Table.Column></Table.Column>
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
                          <Button onPress={() => handleView(customer.id)}>
                            <FaArrowUpRightFromSquare />
                            View profile
                          </Button>
                        </Table.Cell>
                        <Table.Cell>
                          <Button
                            onPress={() => handleDelete(customer.id)}
                            variant="danger"
                          >
                            Delete
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
                    <Table.Column></Table.Column>
                    <Table.Column></Table.Column>
                  </Table.Header>
                  <EmptyTable
                    data={data.employees}
                    renderRow={(employee) => (
                      <Table.Row key={employee.id}>
                        <Table.Cell>{employee.id}</Table.Cell>
                        <Table.Cell>{employee.email}</Table.Cell>
                        <Table.Cell>{employee.address}</Table.Cell>
                        <Table.Cell>{employee.numOfLoyaltyPoints}</Table.Cell>
                        <Table.Cell>
                          {employee.purchasedOrders.length}
                        </Table.Cell>
                        <Table.Cell>
                          {employee.assignedOrders.length}
                        </Table.Cell>
                        <Table.Cell>
                          <Button onPress={() => handleView(employee.id)}>
                            <FaArrowUpRightFromSquare />
                            View profile
                          </Button>
                        </Table.Cell>
                        <Table.Cell>
                          <Button
                            onPress={() => handleDelete(employee.id)}
                            variant="danger"
                          >
                            Delete
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
