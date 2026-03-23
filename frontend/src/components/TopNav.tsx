import { useState } from "react";
import { Link } from "@tanstack/react-router";
import { Button } from "@heroui/react";

type TopNavProps = {
  isLoggedIn: boolean;
  logout?: () => void;
};

export default function TopNav({ isLoggedIn, logout }: TopNavProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <nav className="sticky top-0 z-40 w-full border-b border-separator bg-background/70 backdrop-blur-lg">
      <header className="mx-auto flex h-16 max-w-5xl items-center justify-between px-6">
        <div className="flex items-center gap-4">
          <button
            className="md:hidden hover:cursor-pointer"
            onClick={() => setIsMenuOpen(!isMenuOpen)}
            aria-label="Toggle menu"
            aria-expanded={isMenuOpen}
          >
            <span className="sr-only">Menu</span>
            <svg
              className="h-6 w-6"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              {isMenuOpen ? (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              ) : (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              )}
            </svg>
          </button>
          <Link to="/">
            <div className="flex items-center gap-3">
              <img src="/logo192.png" alt="logo" width={50} />
              <p className="font-bold">Fashion Store</p>
            </div>
          </Link>
        </div>
        <ul className="hidden items-center gap-4 md:flex">
          <li>
            <Link to="/">Features</Link>
          </li>
          <li>
            <Link
              to="/"
              className="font-medium text-accent"
              aria-current="page"
            >
              Dashboard
            </Link>
          </li>
          <li>
            <Link to="/">Pricing</Link>
          </li>
        </ul>
        <div className="hidden items-center gap-4 md:flex">
          {isLoggedIn ? (
            <>
              <Link to="/account">My Account</Link>
              <Button onClick={logout}>Log out</Button>
            </>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link to="/register">
                <Button>Sign Up</Button>
              </Link>
            </>
          )}
        </div>
      </header>
      {isMenuOpen && (
        <div className="border-t border-separator md:hidden">
          <ul className="flex flex-col gap-2 p-4">
            <li>
              <Link to="/" className="block py-2">
                Features
              </Link>
            </li>
            <li>
              <Link to="/" className="block py-2 font-medium text-accent">
                Dashboard
              </Link>
            </li>
            <li>
              <Link to="/" className="block py-2">
                Pricing
              </Link>
            </li>
            <li className="mt-4 flex flex-col gap-2 border-t border-separator pt-4">
              {isLoggedIn ? (
                <>
                  <Link to="/account">My Account</Link>
                  <Button className="w-full" onClick={logout}>
                    Log out
                  </Button>
                </>
              ) : (
                <>
                  <Link to="/login" className="block py-2">
                    Login
                  </Link>
                  <Link to="/register" className="w-full">
                    <Button className="w-full">Sign Up</Button>
                  </Link>
                </>
              )}
            </li>
          </ul>
        </div>
      )}
    </nav>
  );
}
