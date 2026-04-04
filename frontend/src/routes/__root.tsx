import { createRootRouteWithContext } from "@tanstack/react-router";
import type { AuthContext } from "#/auth";

import "../styles.css";
import NotFoundPage from "#/components/NotFoundPage";
import RootRoute from "#/components/RootRoute";

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
      { title: "Home | Stilton's Store" },
    ],
    links: [
      {
        rel: "icon",
        href: "/stiltonslogo.png",
      },
    ],
  }),
  component: RootRoute,
  notFoundComponent: NotFoundPage,
});
