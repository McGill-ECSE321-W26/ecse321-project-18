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
  return <h2 className="text-xl">Dashboard</h2>;
}
