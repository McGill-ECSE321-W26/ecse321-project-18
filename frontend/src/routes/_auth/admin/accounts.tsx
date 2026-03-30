import { createFileRoute } from "@tanstack/react-router";
import { Button, Table } from "@heroui/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import type {
  CustomerResponse,
  EmployeeResponse,
  OwnerResponse,
} from "#/types/api";
import Skeleton from "#/components/Skeleton";
import { useAccounts } from "#/utils/helpers";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/accounts")({
  head: () => ({
    meta: [
      {
        title: "Accounts | Fashion Store",
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
      <h2 className="text-xl">Accounts</h2>
      <div>
        <div>
          <h2>
            <a href="#owners">Owners</a>
          </h2>
          <Table>
            <Table.ScrollContainer>
              <Table.Content aria-label="Owner table">
                <Table.Header>
                  <Table.Column>ID</Table.Column>
                  <Table.Column>Email</Table.Column>
                  <Table.Column>Button</Table.Column>
                </Table.Header>
                <Table.Body>
                  {data.owners.map((owner: OwnerResponse) => {
                    return (
                      <Table.Row key={owner.id}>
                        <Table.Cell>{owner.id}</Table.Cell>
                        <Table.Cell>{owner.email}</Table.Cell>
                        <Table.Cell>
                          <Button onPress={() => handlePress(owner.id)}>
                            View profile
                          </Button>
                        </Table.Cell>
                      </Table.Row>
                    );
                  })}
                </Table.Body>
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>

        <div>
          <h2>
            <a href="#customers">Customers</a>
          </h2>
          <Table>
            <Table.ScrollContainer>
              <Table.Content aria-label="Customer table">
                <Table.Header>
                  <Table.Column>ID</Table.Column>
                  <Table.Column>Email</Table.Column>
                  <Table.Column>Address</Table.Column>
                  <Table.Column>Number of Purchased Orders</Table.Column>
                  <Table.Column>Loyalty Points</Table.Column>
                  <Table.Column>Button</Table.Column>
                </Table.Header>
                <Table.Body>
                  {data.customers.map((customer: CustomerResponse) => {
                    return (
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
                    );
                  })}
                </Table.Body>
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>

        <div>
          <h2>
            <a href="#employees">Employees</a>
          </h2>
          <Table>
            <Table.ScrollContainer>
              <Table.Content aria-label="Employee table">
                <Table.Header>
                  <Table.Column>ID</Table.Column>
                  <Table.Column>Email</Table.Column>
                  <Table.Column>Address</Table.Column>
                  <Table.Column>Loyalty Points</Table.Column>
                  <Table.Column>Number of Purchased Orders</Table.Column>
                  <Table.Column>Number of Assigned Orders</Table.Column>
                  <Table.Column>Button</Table.Column>
                </Table.Header>
                <Table.Body>
                  {data.employees.map((employee: EmployeeResponse) => {
                    return (
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
                    );
                  })}
                </Table.Body>
              </Table.Content>
            </Table.ScrollContainer>
          </Table>
        </div>
      </div>
    </>
  );
}
