import { LogOut, Monitor } from "lucide-react";

interface SessionPanelProps {
  onLogout: () => void;
}

export function SessionPanel({ onLogout }: SessionPanelProps) {
  return (
    <section className="rounded-2xl border border-danger/25 bg-surface p-5 sm:p-6">
      <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-danger/25 bg-danger/10 text-danger">
        <LogOut aria-hidden="true" size={19} strokeWidth={1.8} />
      </div>
      <h2 className="mt-5 text-base font-semibold text-ink">Session</h2>
      <p className="mt-2 text-sm leading-6 text-muted">
        Sign out of this browser and remove the access token used by your current session.
      </p>

      <div className="mt-5 flex items-center gap-3 rounded-xl border border-line bg-canvas/50 p-3.5">
        <Monitor aria-hidden="true" size={18} strokeWidth={1.8} className="text-subtle" />
        <div>
          <p className="text-sm font-medium text-ink">Current browser</p>
          <p className="mt-0.5 text-xs text-subtle">Active JWT session</p>
        </div>
      </div>

      <button
        type="button"
        onClick={onLogout}
        className="mt-5 inline-flex min-h-11 w-full items-center justify-center gap-2 rounded-xl border border-danger/35 bg-danger/10 px-4 py-3 text-sm font-semibold text-red-200 transition-colors hover:bg-danger/15"
      >
        <LogOut aria-hidden="true" size={17} strokeWidth={1.8} />
        Log out
      </button>
    </section>
  );
}
