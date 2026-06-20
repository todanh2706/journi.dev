import { LogOut, Monitor } from "lucide-react";

interface SessionPanelProps {
  onLogout: () => void;
}

export function SessionPanel({ onLogout }: SessionPanelProps) {
  return (
    <section className="rounded-2xl border border-red-400/15 bg-[#141522] p-5 sm:p-6">
      <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-red-400/10 bg-red-400/[0.07] text-red-300">
        <LogOut aria-hidden="true" size={19} strokeWidth={1.8} />
      </div>
      <h2 className="mt-5 text-base font-semibold text-gray-100">Session</h2>
      <p className="mt-2 text-sm leading-6 text-gray-500">
        Sign out of this browser and remove the access token used by your current session.
      </p>

      <div className="mt-5 flex items-center gap-3 rounded-xl border border-white/[0.06] bg-black/10 p-3.5">
        <Monitor aria-hidden="true" size={18} strokeWidth={1.8} className="text-gray-500" />
        <div>
          <p className="text-sm font-medium text-gray-300">Current browser</p>
          <p className="mt-0.5 text-xs text-gray-600">Active JWT session</p>
        </div>
      </div>

      <button
        type="button"
        onClick={onLogout}
        className="mt-5 inline-flex w-full cursor-pointer items-center justify-center gap-2 rounded-xl border border-red-400/25 bg-red-400/[0.08] px-4 py-3 text-sm font-semibold text-red-200 transition-colors hover:border-red-400/40 hover:bg-red-400/[0.13] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-400/40"
      >
        <LogOut aria-hidden="true" size={17} strokeWidth={1.8} />
        Log out
      </button>
    </section>
  );
}
