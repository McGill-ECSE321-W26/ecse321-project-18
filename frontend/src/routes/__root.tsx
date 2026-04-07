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
import { successToast } from "#/utils/helpers";

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
          successToast("Logged out successfully.");
          navigate({ to: "/" });
        });
      });
    };
    return (
      <>
        <HeadContent />
        <Toast.Provider className="whitespace-pre-wrap" />
        <TopNav account={auth.user?.accountType} logout={handleLogout} />
        <div className="flex flex-col">
          <main className="px-6 pb-8 pt-14 flex-1">
            <Outlet />
          </main>
          <Footer />
        </div>
      </>
    );
  },
  notFoundComponent: NotFoundPage,
});
