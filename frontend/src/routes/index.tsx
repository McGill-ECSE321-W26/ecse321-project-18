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
  component: () => <HomePage />,
});
