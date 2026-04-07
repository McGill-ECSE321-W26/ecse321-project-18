import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { Button, Card, Input, Label, Table } from "@heroui/react";
import type {
  AccountListResponse,
  CustomerResponse,
  EmployeeResponse,
  OwnerResponse,
} from "#/types/api";
import { AccountType } from "#/types/api";
import CustomSkeleton from "#/components/CustomSkeleton";
import { useAccounts } from "#/utils/helpers";
import Title from "#/components/Title";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/accounts/$accountId")({
  loader: ({ params }) => params.accountId,
  head: ({ loaderData }) => ({
    meta: [
      {
        title: `View account ${loaderData} | Stilton's Store`,
      },
    ],
  }),
  component: () => (
    <QueryClientProvider client={queryClient}>
      <ManageAccount />
    </QueryClientProvider>
  ),
});

function ManageAccount() {
  const navigate = useNavigate();
  const { accountId } = Route.useParams();
  const id = Number(accountId);
  const { isLoading, error, account } = useAccount(id);

  if (isLoading) return <CustomSkeleton />;

  if (error) {
    return (
      <div className="max-w-2xl space-y-2">
        <h2 className="text-xl font-bold">Could not load account</h2>
        <p className="text-red-600">{error.message}</p>
      </div>
    );
  }

  if (!account) {
    return (
      <div className="max-w-2xl space-y-2">
        <h2 className="text-xl font-bold">Could not load account</h2>
        <p className="text-red-600">No account with ID {id} exists.</p>
        <Button
          variant="secondary"
          onPress={() => navigate({ to: "/admin/accounts" })}
        >
          Back
        </Button>
      </div>
    );
  }

  return (
    <div className="-mt-12 mx-auto max-w-2xl flex flex-col gap-4">
      <Title pagename={`Account ID ${id}`} />

      <div className="flex flex-col gap-4 rounded-xl border border-default-200 p-6">
        <h3 className="text-xl font-bold">Profile</h3>

        <div className="flex flex-col gap-2 text-base">
          <div>
            <p className="text-sm font-bold">ID</p>
            <p>{account.id}</p>
          </div>
          <div>
            <p className="text-sm font-bold">Email</p>
            <p>{account.email}</p>
          </div>
          <div>
            <p className="text-sm font-bold">Account type</p>
            <p>{account.accountType}</p>
          </div>
        </div>
      </div>

      {account.accountType === AccountType.CUSTOMER ||
      account.accountType === AccountType.EMPLOYEE ? (
        <div className="flex flex-col gap-4 rounded-xl border border-default-200 p-6">
          <h3 className="text-xl font-bold">Customer details</h3>

          <div className="text-base flex flex-col gap-2">
            <div>
              <p className="text-sm font-bold">Address</p>
              <p>{account.address}</p>
            </div>
            <div>
              <p className="text-sm font-bold">Loyalty points</p>
              <p>{account.numOfLoyaltyPoints}</p>
            </div>
            <div>
              <p className="text-sm font-bold">Shopping cart items</p>
              <p>{account.shoppingCartItems.length}</p>
            </div>
            <div>
              <p className="text-sm font-bold">Purchased orders</p>
              <p>{account.purchasedOrders.length}</p>
            </div>
          </div>
        </div>
      ) : null}

      {account.accountType === AccountType.EMPLOYEE ? (
        <div className="flex flex-col gap-4 rounded-xl border border-default-200 p-6">
          <h3 className="text-xl font-bold">Employee details</h3>
          <div className="text-base flex flex-col gap-2">
            <p className="text-sm">
              <strong>Number of assigned orders:</strong>{" "}
              {account.assignedOrders.length}
            </p>

            {account.assignedOrders.length === 0 ? (
              <p className="text-gray-600">No assigned orders.</p>
            ) : (
              <Table className="mt-2">
                <Table.ScrollContainer>
                  <Table.Content aria-label="Assigned orders table">
                    <Table.Header>
                      <Table.Column isRowHeader>ID</Table.Column>
                      <Table.Column>State</Table.Column>
                      <Table.Column>Price</Table.Column>
                      <Table.Column>Delivery Date</Table.Column>
                      <Table.Column>Delivery Address</Table.Column>
                      <Table.Column>Order Date</Table.Column>
                    </Table.Header>
                    <Table.Body>
                      {account.assignedOrders.map((order) => (
                        <Table.Row key={order.id}>
                          <Table.Cell>{order.id}</Table.Cell>
                          <Table.Cell>{order.state}</Table.Cell>
                          <Table.Cell>${order.price.toFixed(2)}</Table.Cell>
                          <Table.Cell>
                            {order.deliveryDate.toString()}
                          </Table.Cell>
                          <Table.Cell>{order.deliveryAddress}</Table.Cell>
                          <Table.Cell>{order.orderDate.toString()}</Table.Cell>
                        </Table.Row>
                      ))}
                    </Table.Body>
                  </Table.Content>
                </Table.ScrollContainer>
              </Table>
            )}
          </div>
        </div>
      ) : null}
    </div>
  );
}

type AccountDetail =
  | (OwnerResponse & { accountType: AccountType.OWNER })
  | (CustomerResponse & { accountType: AccountType.CUSTOMER })
  | (EmployeeResponse & { accountType: AccountType.EMPLOYEE });

function useAccount(id: number) {
  const { isLoading, error, data } = useAccounts();
  const account = data ? findAccountById(data, id) : undefined;
  return { isLoading, error, account };
}

function findAccountById(
  data: AccountListResponse,
  id: number,
): AccountDetail | undefined {
  const owner = data.owners.find((account) => account.id === id);
  if (owner) {
    return { ...owner, accountType: AccountType.OWNER };
  }
  const customer = data.customers.find((account) => account.id === id);
  if (customer) {
    return { ...customer, accountType: AccountType.CUSTOMER };
  }
  const employee = data.employees.find((account) => account.id === id);
  if (employee) {
    return { ...employee, accountType: AccountType.EMPLOYEE };
  }

  return undefined;
}
