import {
  QueryClient,
  QueryClientProvider,
  useQuery,
} from "@tanstack/react-query";
import { createFileRoute } from "@tanstack/react-router";
import Skeleton from "#/components/Skeleton";
import { getRequest } from "#/utils/httpClient";

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

function useAccount(id: number) {
  return useQuery({
    queryKey: ["accountAdminView"],
    queryFn: () => getRequest(`/account/${id}`),
  });
}

function ManageAccount() {
  const { accountId }: { accountId: number } = Route.useParams();

  const { isLoading, error, data } = useAccount(accountId);

  if (isLoading) return <Skeleton />;

  if (error) return "An error has occurred: " + error.message;

  return (
    <div>
      <h2>Account ID: {accountId}</h2>
    </div>
  );
}
