import { createFileRoute } from "@tanstack/react-router";
import HomePage from "#/components/HomePage";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      {
        title: "Stilton's Store",
      },
    ],
  }),
  component: () => <App />,
});

function App() {
  return (
    <>
      <main className="px-4 pb-8 pt-14">
        <HomePage />
      </main>
    </>
  );
}
