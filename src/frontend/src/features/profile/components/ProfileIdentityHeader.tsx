import { ShieldCheck } from "lucide-react";
import { UserAvatar } from "../../../components/UserAvatar/UserAvatar";

interface ProfileIdentityHeaderProps {
  username: string;
}

export function ProfileIdentityHeader({ username }: ProfileIdentityHeaderProps) {
  return (
    <section className="app-panel flex flex-col gap-5 p-5 sm:flex-row sm:items-center sm:justify-between sm:p-6">
      <div className="flex min-w-0 items-center gap-4">
        <UserAvatar username={username} className="h-16 w-16 text-xl" />
        <div className="min-w-0">
          <h2 className="truncate text-xl font-semibold tracking-[-0.02em] text-ink">
            {username}
          </h2>
          <p className="mt-1 truncate text-sm text-muted">@{username}</p>
        </div>
      </div>

      <div className="flex items-center gap-3 rounded-xl border border-success/25 bg-success/10 px-3.5 py-3 text-green-200">
        <ShieldCheck aria-hidden="true" size={18} strokeWidth={1.8} />
        <div>
          <p className="text-xs font-semibold">Active session</p>
          <p className="mt-0.5 text-xs text-green-200/60">JWT authentication</p>
        </div>
      </div>
    </section>
  );
}
