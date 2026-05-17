import { type ReactNode } from "react";

type MilestoneCardProps = {
  title: string;
  id: string | number;
  progress: number;
  status: string;
  icon: ReactNode;
  locked?: boolean;
};

export function MilestoneCard({ title, id, progress, status, icon, locked }: MilestoneCardProps) {
  return (
    <div className={`bg-[#141527] border border-white/[0.06] rounded-2xl p-6 flex flex-col ${locked ? 'opacity-50 grayscale-[0.5]' : ''}`}>
      <div className="flex items-start justify-between mb-8">
        <div className={`w-11 h-11 rounded-xl flex items-center justify-center border border-white/[0.04] ${locked ? 'bg-white/[0.02]' : 'bg-[#1c1d33] shadow-inner shadow-white/[0.02]'}`}>
          {icon}
        </div>
        <div className={`text-[11px] font-semibold px-2.5 py-1 rounded-md ${
          status === 'In Progress' ? 'bg-indigo-500/15 text-indigo-300 border border-indigo-500/20' :
          status === 'Locked' ? 'bg-white/[0.03] text-gray-500 border border-white/[0.05]' :
          'bg-white/[0.06] text-gray-300 border border-white/[0.05]'
        }`}>
          {status}
        </div>
      </div>
      <div className="font-semibold text-[15px] mb-1.5 text-gray-100">{title}</div>
      <div className="text-[13px] text-gray-500 mb-6 font-mono">ID: {id}</div>
      
      <div className="mt-auto">
        <div className="flex justify-between text-[13px] mb-2.5">
          <span className="text-gray-500">Progress</span>
          <span className={locked ? "text-gray-500" : "text-indigo-400 font-medium"}>{progress}%</span>
        </div>
        <div className="h-2 w-full bg-[#1c1d33] rounded-full overflow-hidden border border-white/[0.02]">
          <div 
            className="h-full bg-indigo-500 rounded-full"
            style={{ width: `${progress}%` }}
          />
        </div>
      </div>
    </div>
  );
}