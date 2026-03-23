/* based very heavily on Tanstack's example with authenticated routes:
https://github.com/TanStack/router/tree/main/examples/react/authenticated-routes
*/
import * as React from "react";

import { sleep } from "./utils/helpers";
import type { AccountResponse } from "./types/api";

export interface AuthContext {
  isAuthenticated: boolean;
  login: (account: AccountResponse) => Promise<void>;
  logout: () => Promise<void>;
  user: AccountResponse | null;
}

const AuthContext = React.createContext<AuthContext | null>(null);

const key = "fashionstore.auth.user";

function getStoredUser() {
  const storedUser = localStorage.getItem(key);
  return storedUser ? JSON.parse(storedUser) : null;
}

function setStoredUser(user: AccountResponse | null) {
  if (user) {
    localStorage.setItem(key, JSON.stringify(user));
  } else {
    localStorage.removeItem(key);
  }
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = React.useState<AccountResponse | null>(
    getStoredUser(),
  );
  const isAuthenticated = !!user;

  const logout = React.useCallback(async () => {
    await sleep(250);

    setStoredUser(null);
    setUser(null);
  }, []);

  /* actual login request will be handled by the login page/form! this just updates context */
  const login = React.useCallback(async (account: AccountResponse) => {
    await sleep(100);

    setStoredUser(account);
    setUser(account);
  }, []);

  // update/check auth status/user on mount
  React.useEffect(() => {
    setUser(getStoredUser);
  }, []);

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = React.useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
}
