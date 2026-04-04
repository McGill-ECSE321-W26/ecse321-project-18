import {
  HeadContent,
  Outlet,
  useNavigate,
  useRouter,
} from "@tanstack/react-router";
import { TanStackRouterDevtoolsPanel } from "@tanstack/react-router-devtools";
import { TanStackDevtools } from "@tanstack/react-devtools";
import { Toast } from "@heroui/react";
import Footer from "#/components/Footer";
import TopNav from "#/components/TopNav";
import { useAuth } from "#/auth";

export default function RootRoute() {
  const router = useRouter();
  const auth = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    auth.logout().then(() => {
      router.invalidate().finally(() => {
        navigate({ to: "/" });
      });
    });
  };

  return (
    <>
      <HeadContent />
      <Toast.Provider className="whitespace-pre-wrap" />
      <TopNav account={auth.user?.accountType} logout={handleLogout} />
      <Outlet />
      <Footer />

      <TanStackDevtools
        config={{
          position: "bottom-right",
        }}
        plugins={[
          {
            name: "TanStack Router",
            render: <TanStackRouterDevtoolsPanel />,
          },
        ]}
      />
    </>
  );
}
