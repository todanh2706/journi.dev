import { LockKeyhole, UserRound } from "lucide-react";

import { SettingsField } from "./SettingsField";

interface AccountInformationPanelProps {
  username: string;
}

export function AccountInformationPanel({ username }: AccountInformationPanelProps) {
  return (
    <section className="app-panel p-5 sm:p-6">
      <div className="flex items-start justify-between gap-4">
        <div className="flex gap-3">
          <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-line-strong bg-surface-elevated text-gold">
            <UserRound aria-hidden="true" size={19} strokeWidth={1.8} />
          </div>
          <div>
            <h2 className="text-base font-semibold text-ink">Personal information</h2>
            <p className="mt-1 max-w-xl text-sm leading-6 text-muted">
              Review the account fields that will be editable when the self-service API is available.
            </p>
          </div>
        </div>
      </div>

      <div className="mt-6 grid gap-5 sm:grid-cols-2">
        <SettingsField
          label="Display name"
          placeholder="Not available"
          autoComplete="name"
          description="Display names are not stored by the backend yet."
        />
        <SettingsField
          label="Username"
          value={username}
          placeholder="Username"
          autoComplete="username"
          description="This value comes from your signed JWT."
        />
        <div className="sm:col-span-2">
          <SettingsField
            label="Email address"
            placeholder="Not available"
            type="email"
            autoComplete="email"
            description="Your email is not included in the current session token."
          />
        </div>
      </div>

      <div className="mt-6 flex flex-col gap-3 border-t border-line pt-5 sm:flex-row sm:items-center sm:justify-between">
        <div className="flex items-center gap-2 text-xs text-warning">
          <LockKeyhole aria-hidden="true" size={15} strokeWidth={1.8} />
          Editing is unavailable until a self-service profile API is added.
        </div>
        <button
          type="button"
          disabled
          className="secondary-button"
        >
          Save changes
        </button>
      </div>
    </section>
  );
}
