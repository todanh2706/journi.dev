import { type ReactNode } from "react";

interface ActionCardProps {
  title: string,
  subtitle: string, 
  icon: ReactNode;
}

export function ActionCard({ title, subtitle, icon }: ActionCardProps) {
  return (
    <button className="bg-[#141527] border border-white/[0.06] hover:border-white/[0.12] hover:bg-[#18192e] rounded-2xl p-5 flex items-center gap-4 text-left transition-all active:scale-[0.98]">
      <div className="w-12 h-12 rounded-xl bg-[#1c1d33] flex items-center justify-center shrink-0 border border-white/[0.03] shadow-inner shadow-white/[0.02]">
        {icon}
      </div>
      <div>
        <div className="font-semibold text-[15px] text-gray-100 mb-1">{title}</div>
        <div className="text-[13px] text-gray-500">{subtitle}</div>
      </div>
    </button>
  );
}