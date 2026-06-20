import { useState, useEffect } from 'react';
import { logout as requestLogout } from '../services/auth';

export interface AuthUser {
    username: string;
    exp: number;
    iat: number;
}

function getInitialUser(): AuthUser | null {
    const token = localStorage.getItem("access_token");
    if (!token) return null;

    try {
        // Decode the payload part of the JWT
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        const decoded = JSON.parse(jsonPayload);
        
        // Check expiration
        const currentTime = Date.now() / 1000;
        if (decoded.exp && decoded.exp > currentTime) {
            return {
                username: decoded.sub, // Spring Security sets subject as username
                exp: decoded.exp,
                iat: decoded.iat
            };
        } else {
            // Token expired
            localStorage.removeItem("access_token");
            return null;
        }
    } catch {
        localStorage.removeItem("access_token");
        return null;
    }
}

export function useAuth() {
    const [user, setUser] = useState<AuthUser | null>(getInitialUser);
    const [isLoading] = useState(false);

    useEffect(() => {
        // Listen to storage events for cross-tab synchronization
        const handleStorageChange = () => {
            setUser(getInitialUser());
        };
        
        window.addEventListener('storage', handleStorageChange);
        return () => window.removeEventListener('storage', handleStorageChange);
    }, []);

    const logout = async () => {
        await requestLogout();
        localStorage.removeItem("access_token");
        setUser(null);
    };

    return { user, isLoading, logout };
}
