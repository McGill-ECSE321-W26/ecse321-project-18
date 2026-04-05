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
import Skeleton from "#/components/Skeleton";
import { useAccounts } from "#/utils/helpers";
import Title from "#/components/Title";

const queryClient = new QueryClient();

export const Route = createFileRoute("/_auth/admin/accounts/$accountId")({
  loader: ({ params }) => params.accountId,
  head: ({ loaderData }) => ({
    meta: [
      {
        title: `Manage account ${loaderData} | Stilton's Store`,
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

  if (isLoading) return <Skeleton />;

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
      <Title pagename="My Account" />

      <Card>
        <Card.Header>
          <h3 className="text-xl font-semibold">Profile</h3>
        </Card.Header>
        <Card.Content className="space-y-3">
          <Label className="font-semibold"> Account type </Label>
          <Input disabled value={account.accountType}></Input>
          <Label className="font-semibold"> Email </Label>
          <Input disabled value={account.email}></Input>
          <Label className="font-semibold"> ID </Label>
          <Input disabled value={String(account.id)}></Input>
        </Card.Content>
      </Card>

      {account.accountType === AccountType.CUSTOMER ||
      account.accountType === AccountType.EMPLOYEE ? (
        <Card>
          <Card.Header>
            <h3 className="text-xl font-semibold">Customer details</h3>
          </Card.Header>
          <Card.Content className="space-y-3">
            <Label className="font-semibold"> Address </Label>
            <Input disabled value={String(account.address)}></Input>
            <Label className="font-semibold"> Loyalty points </Label>
            <Input disabled value={String(account.numOfLoyaltyPoints)}></Input>
            <Label className="font-semibold"> Shopping cart items </Label>
            <Input
              disabled
              value={String(account.shoppingCartItems.length)}
            ></Input>
            <Label className="font-semibold"> Purchased orders </Label>
            <Input
              disabled
              value={String(account.purchasedOrders.length)}
            ></Input>
          </Card.Content>
        </Card>
      ) : null}

      {account.accountType === AccountType.EMPLOYEE ? (
        <Card>
          <Card.Header>
            <h3 className="text-xl font-semibold">Employee details</h3>
          </Card.Header>
          <Card.Content className="space-y-3">
            <Label className="font-semibold">
              {" "}
              Assigned orders: {String(account.assignedOrders.length)}{" "}
            </Label>

            {account.assignedOrders.length === 0 ? (
              <p className="text-sm text-gray-600">No assigned orders.</p>
            ) : (
              <div className="space-y-3">
                <Table>
                  <Table.ScrollContainer>
                    <Table.Content>
                      <Table.Header>
                        <Table.Column>ID</Table.Column>
                        <Table.Column>state</Table.Column>
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
                            <Table.Cell>
                              {formatCurrency(order.price)}
                            </Table.Cell>
                            <Table.Cell>
                              {formatSafeDate(order.deliveryDate)}
                            </Table.Cell>
                            <Table.Cell>{order.deliveryAddress}</Table.Cell>
                            <Table.Cell>
                              {formatSafeDate(order.orderDate)}
                            </Table.Cell>
                          </Table.Row>
                        ))}
                      </Table.Body>
                    </Table.Content>
                  </Table.ScrollContainer>
                  <Table.Footer>{/* Optional footer content */}</Table.Footer>
                </Table>
              </div>
            )}
          </Card.Content>
        </Card>
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

function formatSafeDate(value: string | Date) {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return "N/A";
  }
  return date.toLocaleString();
}

function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-CA", {
    style: "currency",
    currency: "CAD",
  }).format(value);
}
