import { useId } from "react";

interface SettingsFieldProps {
  label: string;
  value?: string;
  placeholder: string;
  type?: "email" | "password" | "text";
  autoComplete?: string;
  description?: string;
}

export function SettingsField({
  label,
  value,
  placeholder,
  type = "text",
  autoComplete,
  description,
}: SettingsFieldProps) {
  const descriptionId = useId();

  return (
    <label className="block space-y-2">
      <span className="text-[13px] font-medium text-ink">{label}</span>
      <input
        type={type}
        value={value ?? ""}
        placeholder={placeholder}
        autoComplete={autoComplete}
        aria-describedby={description ? descriptionId : undefined}
        disabled
        className="app-input text-muted disabled:opacity-70"
      />
      {description && (
        <span id={descriptionId} className="block text-xs leading-5 text-subtle">
          {description}
        </span>
      )}
    </label>
  );
}
