import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/_auth/employee/")({
  head: () => ({
    meta: [
      {
        title: "Manage orders | Fashion Store",
      },
    ],
  }),
  component: EmployeeDashboard,
});

function EmployeeDashboard() {
  return <h2 className="text-xl">Manage orders</h2>;
}
