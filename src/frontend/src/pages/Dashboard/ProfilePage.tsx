import { useCallback, useState } from "react";
import { LoaderCircle } from "lucide-react";
import { Navigate, useNavigate } from "react-router-dom";

import { useAuth } from "../../features/auth";
import {
  AccountInformationPanel,
  LogoutConfirmationDialog,
  PasswordPanel,
  ProfileIdentityHeader,
  SessionPanel,
} from "../../features/profile";

export default function ProfilePage() {
  const navigate = useNavigate();
  const { user, isLoading, logout } = useAuth();
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false);
  const [logoutPending, setLogoutPending] = useState(false);
  const [logoutError, setLogoutError] = useState<string | null>(null);

  const closeLogoutDialog = useCallback(() => {
    if (logoutPending) return;
    setLogoutDialogOpen(false);
    setLogoutError(null);
  }, [logoutPending]);

  const confirmLogout = async () => {
    if (logoutPending) return;

    setLogoutPending(true);
    setLogoutError(null);

    const serverRevoked = await logout();
    navigate("/signin", {
      replace: true,
      state: serverRevoked
        ? undefined
        : { logoutWarning: "You are signed out locally, but server revocation could not be confirmed. Avoid this device until connectivity returns." },
    });
  };

  if (isLoading) {
    return (
      <div className="flex min-h-[50vh] items-center justify-center text-muted" aria-live="polite">
        <LoaderCircle aria-hidden="true" size={24} className="animate-spin text-gold motion-reduce:animate-none" />
        <span className="ml-3 text-sm">Restoring your session…</span>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/signin" replace />;
  }

  return (
    <div className="mx-auto w-full max-w-[1180px] px-4 py-8 sm:px-6 sm:py-10 lg:px-10 lg:py-12">
      <header className="mb-7">
        <p className="eyebrow">Account</p>
        <h1 className="mt-3 text-3xl font-semibold tracking-[-0.045em] text-ink sm:text-4xl">
          Profile settings
        </h1>
        <p className="mt-3 max-w-2xl text-sm leading-6 text-muted">
          Review your account identity, see upcoming profile controls, and manage your current session.
        </p>
      </header>

      <ProfileIdentityHeader username={user.username} />

      <div className="mt-6 grid gap-6 xl:grid-cols-[minmax(0,1.55fr)_minmax(300px,0.72fr)] xl:items-start">
        <div className="space-y-6">
          <AccountInformationPanel username={user.username} />
          <PasswordPanel />
        </div>
        <SessionPanel
          onLogout={() => {
            setLogoutError(null);
            setLogoutDialogOpen(true);
          }}
        />
      </div>

      <LogoutConfirmationDialog
        open={logoutDialogOpen}
        pending={logoutPending}
        error={logoutError}
        onCancel={closeLogoutDialog}
        onConfirm={confirmLogout}
      />
    </div>
  );
}
