import { type ReactNode } from "react";

export function SocialButton({ icon, label, id }: { icon: ReactNode; label: string; id: string }) {
    return (
        <button
            id={id}
            className="flex items-center justify-center gap-2.5 w-full bg-white/[0.03] border border-white/[0.08] text-gray-300 font-medium text-sm py-3 rounded-xl hover:bg-white/[0.07] hover:border-white/[0.14] transition-all duration-200 cursor-pointer active:scale-[0.98]"
        >
            {icon}
            {label}
        </button>
    );
}