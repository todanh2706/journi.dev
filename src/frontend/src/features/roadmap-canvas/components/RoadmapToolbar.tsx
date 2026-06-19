import { Search, Target } from "lucide-react";

type RoadmapToolbarProps = {
  completedCount: number;
  totalCount: number;
  searchTerm: string;
  onSearchTermChange: (value: string) => void;
  onFitView: () => void;
};

export function RoadmapToolbar({
  completedCount,
  totalCount,
  searchTerm,
  onSearchTermChange,
  onFitView,
}: RoadmapToolbarProps) {
  return (
    <div className="flex w-[min(42rem,calc(100vw-3rem))] flex-col gap-3 rounded-2xl border border-white/[0.08] bg-slate-950/85 p-3 shadow-2xl shadow-black/30 backdrop-blur-xl sm:flex-row sm:items-center">
      <div className="flex items-center justify-between gap-3 sm:justify-start">
        <div className="rounded-xl border border-emerald-300/15 bg-emerald-300/10 px-3 py-2">
          <p className="text-[10px] font-bold uppercase tracking-[0.14em] text-emerald-200/70">Progress</p>
          <p className="text-sm font-bold text-emerald-100">
            {completedCount} / {totalCount} completed
          </p>
        </div>
        <button
          type="button"
          onClick={onFitView}
          className="inline-flex h-10 w-10 shrink-0 items-center justify-center rounded-xl border border-white/[0.08] bg-white/[0.04] text-slate-200 transition hover:border-violet-300/40 hover:bg-violet-400/10 hover:text-violet-100"
          aria-label="Fit roadmap graph"
          title="Fit view"
        >
          <Target size={18} />
        </button>
      </div>

      <label className="relative min-w-0 flex-1">
        <span className="sr-only">Search roadmap nodes</span>
        <Search size={16} className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-slate-500" />
        <input
          value={searchTerm}
          onChange={(event) => onSearchTermChange(event.target.value)}
          placeholder="Search nodes"
          className="h-10 w-full rounded-xl border border-white/[0.08] bg-white/[0.04] pl-9 pr-3 text-sm text-slate-100 outline-none transition placeholder:text-slate-500 focus:border-cyan-300/40 focus:bg-white/[0.06]"
        />
      </label>
    </div>
  );
}
