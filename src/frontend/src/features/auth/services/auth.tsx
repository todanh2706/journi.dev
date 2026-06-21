import { AxiosError } from "axios";

import api, { registerAuthRefreshHandler } from "../../../services/axios";
import { AUTH_ENDPOINTS } from "../../../services/authEndpoints";
import { setAccessToken } from "../../../services/authSession";

export interface SignupRequest {
    username: string;
    email: string;
    password: string;
}

export interface SignupResponse {
    userId: string;
    username: string;
    email: string;
    role: string;
    status: string;
    createdAt: string;
    updatedAt: string;
    deletedAt: string | null;
}

interface ApiErrorPayload {
    detail?: string;
    message?: string;
    error?: string;
    validationErrors?: Record<string, string>;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    token: string;
    expiresIn: number;
}

export interface CsrfResponse {
    headerName: string;
    token: string;
}

interface LockManagerLike {
    request<T>(name: string, callback: () => Promise<T>): Promise<T>;
}

let csrfMaterial: CsrfResponse | null = null;
let refreshPromise: Promise<string> | null = null;

export async function signup(payload: SignupRequest): Promise<SignupResponse> {
    const response = await api.post<SignupResponse>(AUTH_ENDPOINTS.signup, payload, {
        skipAuthRefresh: true,
    });
    return response.data;
}

export async function login(payload: LoginRequest): Promise<LoginResponse> {
    const csrf = await getCsrfMaterial();
    const response = await api.post<LoginResponse>(AUTH_ENDPOINTS.login, payload, authMutationConfig(csrf));
    setAccessToken(response.data.token);
    return response.data;
}

export async function logout(): Promise<void> {
    const csrf = await getCsrfMaterial();
    await api.post(AUTH_ENDPOINTS.logout, undefined, authMutationConfig(csrf));
}

export function refreshAccessToken(): Promise<string> {
    refreshPromise ??= withCrossTabRefreshLock(async () => {
        const csrf = await getCsrfMaterial();
        const response = await api.post<LoginResponse>(
            AUTH_ENDPOINTS.refresh,
            undefined,
            authMutationConfig(csrf),
        );
        setAccessToken(response.data.token);
        return response.data.token;
    }).finally(() => {
        refreshPromise = null;
    });

    return refreshPromise;
}

async function getCsrfMaterial(): Promise<CsrfResponse> {
    if (csrfMaterial) return csrfMaterial;

    const response = await api.get<CsrfResponse>(AUTH_ENDPOINTS.csrf, {
        withCredentials: true,
        skipAuthRefresh: true,
    });
    csrfMaterial = response.data;
    return csrfMaterial;
}

function authMutationConfig(csrf: CsrfResponse) {
    return {
        withCredentials: true,
        skipAuthRefresh: true,
        headers: {
            [csrf.headerName]: csrf.token,
        },
    };
}

async function withCrossTabRefreshLock<T>(operation: () => Promise<T>): Promise<T> {
    const lockManager = typeof navigator === "undefined"
        ? undefined
        : (navigator as Navigator & { locks?: LockManagerLike }).locks;
    return lockManager
        ? lockManager.request("journi-refresh-session", operation)
        : operation();
}

registerAuthRefreshHandler(refreshAccessToken);

export function getSignupErrorMessage(error: unknown): string {
    if (error instanceof AxiosError) {
        const data = error.response?.data as ApiErrorPayload | string | undefined;

        if (typeof data === "object" && data?.validationErrors) {
            const firstError = Object.values(data.validationErrors)[0];
            if (firstError) return firstError;
        }

        const detail =
            typeof data === "string"
                ? data
                : data?.detail ?? data?.message ?? data?.error ?? "";
        const normalizedDetail = detail.toLowerCase();

        if (normalizedDetail.includes("username is already taken")) {
            return "That username is already taken. Try another one.";
        }

        if (normalizedDetail.includes("email is already registered") || normalizedDetail.includes("email is already taken")) {
            return "That email is already registered. Try signing in instead.";
        }

        if (error.response?.status === 400) {
            return "Please review your signup details and try again.";
        }
    }

    return "We couldn't create your account right now. Please try again.";
}
