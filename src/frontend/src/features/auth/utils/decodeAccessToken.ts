import type { AuthUser } from "../AuthContext";

export function decodeAccessToken(token: string | null): AuthUser | null {
  if (!token) return null;

  try {
    const payloadPart = token.split(".")[1];
    if (!payloadPart) return null;
    const padded = payloadPart.replace(/-/g, "+").replace(/_/g, "/")
      .padEnd(Math.ceil(payloadPart.length / 4) * 4, "=");
    const decoded: unknown = JSON.parse(window.atob(padded));
    if (!isAuthPayload(decoded) || decoded.exp <= Date.now() / 1000) return null;
    return {
      username: decoded.sub,
      exp: decoded.exp,
      iat: decoded.iat,
    };
  } catch {
    return null;
  }
}

function isAuthPayload(value: unknown): value is { sub: string; exp: number; iat: number } {
  return typeof value === "object"
    && value !== null
    && "sub" in value
    && typeof value.sub === "string"
    && "exp" in value
    && typeof value.exp === "number"
    && "iat" in value
    && typeof value.iat === "number";
}
