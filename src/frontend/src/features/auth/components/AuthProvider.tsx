import { useCallback, useEffect, useMemo, useState, type ReactNode } from "react";

import { endSession, getAccessToken, subscribeAccessToken } from "../../../services/authSession";
import { AuthContext, type AuthUser } from "../AuthContext";
import {
  login as requestLogin,
  logout as requestLogout,
  refreshAccessToken,
  type LoginRequest,
} from "../services/auth";
import { decodeAccessToken } from "../utils/decodeAccessToken";

let initialSessionPromise: Promise<string | null> | null = null;

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<AuthUser | null>(() => decodeAccessToken(getAccessToken()));
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => subscribeAccessToken((token) => {
    setUser(decodeAccessToken(token));
  }), []);

  useEffect(() => {
    localStorage.removeItem("access_token");
    let active = true;
    initialSessionPromise ??= refreshAccessToken().catch(() => {
      endSession();
      return null;
    });
    void initialSessionPromise.finally(() => {
      if (active) setIsLoading(false);
    });
    return () => {
      active = false;
    };
  }, []);

  const login = useCallback(async (payload: LoginRequest) => {
    const response = await requestLogin(payload);
    setUser(decodeAccessToken(response.token));
  }, []);

  const logout = useCallback(async () => {
    let serverRevoked = true;
    try {
      await requestLogout();
    } catch {
      serverRevoked = false;
    } finally {
      endSession({ broadcast: true });
    }
    return serverRevoked;
  }, []);

  const value = useMemo(() => ({ user, isLoading, login, logout }), [user, isLoading, login, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
