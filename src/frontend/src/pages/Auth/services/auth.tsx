import { AxiosError } from "axios";

import api from "../../../services/axios";

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

export async function signup(payload: SignupRequest): Promise<SignupResponse> {
    const response = await api.post<SignupResponse>("/auth/signup", payload);
    return response.data;
}

export async function login(payload: LoginRequest): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>("/auth/login", payload);
    return response.data;
}

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
