export const AUTH_ENDPOINTS = {
  signup: "/auth/signup",
  login: "/auth/login",
  refresh: "/auth/refresh",
  logout: "/auth/logout",
  csrf: "/auth/csrf",
} as const;

export const AUTH_ENDPOINT_PATHS = new Set<string>(Object.values(AUTH_ENDPOINTS));

export function normalizeApiPath(url: string | undefined, baseURL: string | undefined): string {
  if (!url) return "/";

  const origin = typeof window === "undefined" ? "http://localhost" : window.location.origin;
  const isAbsolute = /^[a-z][a-z\d+.-]*:\/\//i.test(url);
  const pathname = isAbsolute
    ? new URL(url).pathname
    : new URL(url.startsWith("/") ? url : `/${url}`, origin).pathname;
  const basePath = baseURL
    ? new URL(baseURL, origin).pathname.replace(/\/$/, "")
    : "";
  const withoutBase = basePath && (pathname === basePath || pathname.startsWith(`${basePath}/`))
    ? pathname.slice(basePath.length)
    : pathname;
  const normalized = `/${withoutBase}`.replace(/\/{2,}/g, "/").replace(/\/$/, "");
  return normalized || "/";
}
