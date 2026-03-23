import {
  Outlet,
  createFileRoute,
  redirect,
  useRouter,
} from "@tanstack/react-router";
import { useAuth } from "#/auth";
import TopNav from "#/components/TopNav";

export const Route = createFileRoute("/_auth")({
  // redirect user to login if no one is logged in
  beforeLoad: ({ context }) => {
    if (!context.auth.isAuthenticated) {
      throw redirect({
        to: "/login",
      });
    }
  },
  component: AuthLayout,
});

function AuthLayout() {
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
        <Outlet />
      </main>
    </>
  );
}
