import { createFileRoute } from "@tanstack/react-router";
import { useAuth } from "#/auth";
import TopNav from "#/components/TopNav";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      {
        title: "Home | Fashion Store",
      },
    ],
  }),
  component: () => <App />,
});

function App() {
  const auth = useAuth();

  return (
    <>
      <TopNav isLoggedIn={auth.isAuthenticated} />
      <main className="px-4 pb-8 pt-14">
        <h1>Fashion store</h1>
      </main>
    </>
  );
}
