import { createFileRoute } from "@tanstack/react-router";

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
  return (
    <>
      <main className="px-4 pb-8 pt-14">
        <h1>Fashion store</h1>
      </main>
    </>
  );
}
