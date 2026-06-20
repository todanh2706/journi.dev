import { ShieldCheck } from "lucide-react";

interface ProfileIdentityHeaderProps {
  username: string;
}

export function ProfileIdentityHeader({ username }: ProfileIdentityHeaderProps) {
  return (
    <section className="flex flex-col gap-5 rounded-2xl border border-white/[0.06] bg-[#141522] p-5 shadow-[0_18px_45px_rgba(0,0,0,0.16)] sm:flex-row sm:items-center sm:justify-between sm:p-6">
      <div className="flex min-w-0 items-center gap-4">
        <img
          src={`https://i.pravatar.cc/150?u=${username}`}
          alt={`${username}'s avatar`}
          className="h-16 w-16 shrink-0 rounded-2xl border border-white/10 object-cover ring-1 ring-indigo-400/25"
        />
        <div className="min-w-0">
          <h2 className="truncate text-xl font-semibold tracking-[-0.02em] text-gray-100">
            {username}
          </h2>
          <p className="mt-1 truncate text-sm text-gray-500">@{username}</p>
        </div>
      </div>

      <div className="flex items-center gap-3 rounded-xl border border-emerald-400/10 bg-emerald-400/[0.05] px-3.5 py-3 text-emerald-300/90">
        <ShieldCheck aria-hidden="true" size={18} strokeWidth={1.8} />
        <div>
          <p className="text-xs font-semibold">Active session</p>
          <p className="mt-0.5 text-xs text-emerald-200/50">JWT authentication</p>
        </div>
      </div>
    </section>
  );
}
