import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/admin/")({
  head: () => ({
    meta: [
      {
        title: "Dashboard | Fashion Store",
      },
    ],
  }),
  component: AdminDashboard,
});

function AdminDashboard() {
  return <p>Dashboard</p>;
}
