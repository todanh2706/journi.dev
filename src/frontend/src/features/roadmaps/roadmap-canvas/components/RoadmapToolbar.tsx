import { Search, Target } from "lucide-react";

interface RoadmapToolbarProps {
  completedCount: number;
  totalCount: number;
  matchCount: number;
  searchTerm: string;
  onSearchTermChange: (value: string) => void;
  onFitView: () => void;
}

export function RoadmapToolbar({ completedCount, totalCount, matchCount, searchTerm, onSearchTermChange, onFitView }: RoadmapToolbarProps) {
  const hasSearch = searchTerm.trim().length > 0;

  return (
    <div className="flex w-[min(43rem,calc(100vw-3rem))] flex-col gap-3 rounded-2xl border border-line bg-shell/95 p-3 shadow-xl shadow-black/25 backdrop-blur-md sm:flex-row sm:items-center">
      <div className="flex items-center justify-between gap-3 sm:justify-start">
        <div className="rounded-xl border border-success/25 bg-success/10 px-3 py-2">
          <p className="text-[10px] font-semibold uppercase tracking-[0.13em] text-green-200/70">Progress</p>
          <p className="text-sm font-semibold text-green-100">{completedCount} / {totalCount} completed</p>
        </div>
        <button type="button" onClick={onFitView} className="icon-button" aria-label="Fit roadmap graph" title="Fit view">
          <Target aria-hidden="true" size={18} />
        </button>
      </div>

      <label className="relative min-w-0 flex-1">
        <span className="sr-only">Search roadmap nodes</span>
        <Search aria-hidden="true" size={16} className="pointer-events-none absolute left-3 top-1/2 -translate-y-1/2 text-subtle" />
        <input value={searchTerm} onChange={(event) => onSearchTermChange(event.target.value)} placeholder="Search skill nodes" className="app-input h-11 pl-9 pr-24" />
        {hasSearch ? (
          <span aria-live="polite" className={`absolute right-3 top-1/2 -translate-y-1/2 text-xs font-medium ${matchCount ? "text-gold" : "text-danger"}`}>
            {matchCount ? `${matchCount} found` : "No results"}
          </span>
        ) : null}
      </label>
    </div>
  );
}
