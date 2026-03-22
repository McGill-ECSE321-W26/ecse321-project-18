import { HeadContent, Outlet, createRootRoute } from "@tanstack/react-router";
import { TanStackRouterDevtoolsPanel } from "@tanstack/react-router-devtools";
import { TanStackDevtools } from "@tanstack/react-devtools";

import TopNav from "#/components/TopNav";
import Footer from "#/components/Footer";

import "../styles.css";

export const Route = createRootRoute({
  head: () => ({
    meta: [
      {
        name: "description",
        content: "Fashion store application",
      },
      { title: "Fashion Store" },
    ],
    links: [
      {
        rel: "icon",
        href: "/favicon.ico",
      },
    ],
  }),
  component: () => (
    <>
      <HeadContent />

      <TopNav />
      <main className="page-wrap px-4 pb-8 pt-14">
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
  ),
});
