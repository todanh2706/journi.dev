import { createContext } from "react";

import type { LoginRequest } from "./services/auth";

export interface AuthUser {
  username: string;
  exp: number;
  iat: number;
}

export interface AuthContextValue {
  user: AuthUser | null;
  isLoading: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  logout: () => Promise<boolean>;
}

export const AuthContext = createContext<AuthContextValue | null>(null);
