import { useCallback, useState } from "react";
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
  const { user, logout } = useAuth();
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

    try {
      await logout();
      navigate("/signin", { replace: true });
    } catch {
      setLogoutError("We couldn't log you out right now. Check your connection and try again.");
      setLogoutPending(false);
    }
  };

  if (!user) {
    return <Navigate to="/signin" replace />;
  }

  return (
    <div className="mx-auto w-full max-w-[1180px] px-5 py-8 sm:px-8 lg:px-10 lg:py-10">
      <header className="mb-7">
        <p className="text-sm font-medium text-indigo-300">Account</p>
        <h1 className="mt-2 text-3xl font-semibold tracking-[-0.035em] text-gray-100 sm:text-[34px]">
          Profile settings
        </h1>
        <p className="mt-2 max-w-2xl text-sm leading-6 text-gray-500">
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
