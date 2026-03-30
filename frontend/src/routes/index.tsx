import { createFileRoute, useRouter } from "@tanstack/react-router";
import { useAuth } from "#/auth";
import TopNav from "#/components/TopNav";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      {
        title: "Home | Stilton's Store",
      },
    ],
  }),
  component: () => <App />,
});

function App() {
  const router = useRouter();
  const navigate = Route.useNavigate();
  const auth = useAuth();

  const handleLogout = () => {
    auth.logout().then(() => {
      router.invalidate().finally(() => {
        navigate({ to: "/" });
      });
    });
  };

  return (
    <>
      <TopNav account={auth.user?.accountType} logout={handleLogout} />
      <main className="px-4 pb-8 pt-14">
        <h1>Fashion store</h1>
      </main>
    </>
  );
}
