import { KeyRound, LockKeyhole } from "lucide-react";

import { SettingsField } from "./SettingsField";

export function PasswordPanel() {
  return (
    <section className="rounded-2xl border border-white/[0.06] bg-[#141522] p-5 sm:p-6">
      <div className="flex gap-3">
        <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-indigo-400/10 bg-indigo-400/[0.07] text-indigo-300">
          <KeyRound aria-hidden="true" size={19} strokeWidth={1.8} />
        </div>
        <div>
          <h2 className="text-base font-semibold text-gray-100">Password</h2>
          <p className="mt-1 max-w-xl text-sm leading-6 text-gray-500">
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

      <div className="mt-6 flex flex-col gap-3 border-t border-white/[0.06] pt-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-2 text-xs text-amber-300/70">
          <LockKeyhole aria-hidden="true" size={15} strokeWidth={1.8} />
          Password updates are not connected yet.
        </div>
        <button
          type="button"
          disabled
          className="rounded-xl border border-white/[0.07] bg-white/[0.04] px-4 py-2.5 text-sm font-medium text-gray-600 disabled:cursor-not-allowed"
        >
          Update password
        </button>
      </div>
    </section>
  );
}
