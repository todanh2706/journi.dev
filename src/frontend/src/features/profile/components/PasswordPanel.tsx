import { KeyRound, LockKeyhole } from "lucide-react";

import { SettingsField } from "./SettingsField";

export function PasswordPanel() {
  return (
    <section className="app-panel p-5 sm:p-6">
      <div className="flex gap-3">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
          <KeyRound aria-hidden="true" size={19} strokeWidth={1.8} />
        </div>
        <div>
          <h2 className="text-base font-semibold text-ink">Password</h2>
          <p className="mt-1 max-w-xl text-sm leading-6 text-muted">
            Password changes will require current-password verification before this form can be enabled.
          </p>
        </div>
      </div>

      <div className="mt-6 grid gap-5 sm:grid-cols-2">
        <div className="sm:col-span-2">
          <SettingsField
            label="Current password"
            placeholder="Enter current password"
            type="password"
            autoComplete="current-password"
          />
        </div>
        <SettingsField
          label="New password"
          placeholder="Enter new password"
          type="password"
          autoComplete="new-password"
        />
        <SettingsField
          label="Confirm new password"
          placeholder="Repeat new password"
          type="password"
          autoComplete="new-password"
        />
      </div>

      <div className="mt-6 flex flex-col gap-3 border-t border-line pt-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-2 text-xs text-warning">
          <LockKeyhole aria-hidden="true" size={15} strokeWidth={1.8} />
          Password updates are not connected yet.
        </div>
        <button
          type="button"
          disabled
          className="secondary-button"
        >
          Update password
        </button>
      </div>
    </section>
  );
}
