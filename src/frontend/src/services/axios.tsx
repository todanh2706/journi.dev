import axios, { AxiosError, type InternalAxiosRequestConfig } from "axios";

import { AUTH_ENDPOINT_PATHS, normalizeApiPath } from "./authEndpoints";
import { endSession, getAccessToken } from "./authSession";

declare module "axios" {
  export interface AxiosRequestConfig {
    skipAuthRefresh?: boolean;
  }

  export interface InternalAxiosRequestConfig {
    skipAuthRefresh?: boolean;
    authRetryAttempted?: boolean;
  }
}

type RefreshAccessToken = () => Promise<string>;

let refreshAccessToken: RefreshAccessToken | null = null;
let responseRefreshPromise: Promise<string> | null = null;

const api = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_BASE_URL
});

api.interceptors.request.use((config) => {
    const path = normalizeApiPath(config.url, config.baseURL);
    const token = getAccessToken();

    if (token && !AUTH_ENDPOINT_PATHS.has(path)) {
        config.headers.set("Authorization", `Bearer ${token}`);
    }

    return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const config = error.config;
    if (!shouldAttemptRefresh(error, config) || !refreshAccessToken) {
      throw error;
    }

    config.authRetryAttempted = true;

    try {
      responseRefreshPromise ??= refreshAccessToken().finally(() => {
        responseRefreshPromise = null;
      });
      await responseRefreshPromise;
      return api(config);
    } catch (refreshError) {
      endSession({ broadcast: true });
      throw refreshError;
    }
  },
);

export function registerAuthRefreshHandler(handler: RefreshAccessToken): void {
  refreshAccessToken = handler;
}

function shouldAttemptRefresh(
  error: AxiosError,
  config: InternalAxiosRequestConfig | undefined,
): config is InternalAxiosRequestConfig {
  if (error.response?.status !== 401 || !config || config.authRetryAttempted || config.skipAuthRefresh) {
    return false;
  }

  const path = normalizeApiPath(config.url, config.baseURL);
  return !AUTH_ENDPOINT_PATHS.has(path);
}

export default api;
