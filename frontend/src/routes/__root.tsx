import {
  HeadContent,
  Outlet,
  createRootRouteWithContext,
  useNavigate,
  useRouter,
} from "@tanstack/react-router";

import { TanStackRouterDevtoolsPanel } from "@tanstack/react-router-devtools";
import { TanStackDevtools } from "@tanstack/react-devtools";
import { Toast } from "@heroui/react";
import type { AuthContext } from "#/auth";
import Footer from "#/components/Footer";
import TopNav from "#/components/TopNav";
import { useAuth } from "#/auth";

import "../styles.css";
import NotFoundPage from "#/components/NotFoundPage";

interface FashionStoreContext {
  auth: AuthContext;
}

export const Route = createRootRouteWithContext<FashionStoreContext>()({
  head: () => ({
    meta: [
      {
        name: "description",
        content: "Fashion store application",
      },
      { title: "Stilton's Store" },
    ],
    links: [
      {
        rel: "icon",
        href: "/stiltonslogo.png",
      },
    ],
  }),
  component: () => {
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
        <main className="px-6 pb-8 pt-14">
          <Outlet />
        </main>
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
  },
  notFoundComponent: NotFoundPage,
});
