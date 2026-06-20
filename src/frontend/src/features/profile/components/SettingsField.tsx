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
      <span className="text-[13px] font-medium text-gray-300">{label}</span>
      <input
        type={type}
        value={value ?? ""}
        placeholder={placeholder}
        autoComplete={autoComplete}
        aria-describedby={description ? descriptionId : undefined}
        disabled
        className="w-full rounded-xl border border-white/[0.07] bg-white/[0.025] px-3.5 py-3 text-sm text-gray-400 outline-none placeholder:text-gray-600 disabled:cursor-not-allowed disabled:opacity-80"
      />
      {description && (
        <span id={descriptionId} className="block text-xs leading-5 text-gray-600">
          {description}
        </span>
      )}
    </label>
  );
}
