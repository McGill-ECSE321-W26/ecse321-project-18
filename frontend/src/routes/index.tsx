import { createFileRoute } from "@tanstack/react-router";

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
  return (
    <>
      <h1>Fashion store</h1>
    </>
  );
}
