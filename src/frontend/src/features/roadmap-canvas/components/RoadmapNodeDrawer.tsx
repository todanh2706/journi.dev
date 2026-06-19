import { BookOpen, CheckCircle2, ClipboardList, Lock, X } from "lucide-react";
import type { RoadmapSkillNodeData } from "../types";

type RoadmapNodeDrawerProps = {
  node: RoadmapSkillNodeData | null;
  onClose: () => void;
};

const statusText: Record<RoadmapSkillNodeData["progressStatus"], string> = {
  NOT_STARTED: "Not started",
  IN_PROGRESS: "In progress",
  COMPLETED: "Completed",
};

export function RoadmapNodeDrawer({ node, onClose }: RoadmapNodeDrawerProps) {
  if (!node) return null;

  return (
    <aside className="absolute bottom-3 right-3 top-3 z-20 flex w-[min(24rem,calc(100%-1.5rem))] flex-col overflow-hidden rounded-2xl border border-white/[0.08] bg-slate-950/92 shadow-2xl shadow-black/50 backdrop-blur-2xl">
      <div className="border-b border-white/[0.06] p-5">
        <div className="mb-4 flex items-start justify-between gap-4">
          <div>
            <p className="mb-2 text-[11px] font-bold uppercase tracking-[0.16em] text-violet-200/70">
              Step {node.orderIndex}
            </p>
            <h2 className="text-xl font-bold leading-tight text-slate-50">{node.title}</h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="inline-flex h-9 w-9 shrink-0 items-center justify-center rounded-xl border border-white/[0.08] bg-white/[0.04] text-slate-300 transition hover:border-rose-300/30 hover:bg-rose-400/10 hover:text-rose-100"
            aria-label="Close node details"
            title="Close"
          >
            <X size={18} />
          </button>
        </div>

        <div className="flex flex-wrap gap-2">
          <span className="rounded-full border border-violet-300/20 bg-violet-400/10 px-3 py-1 text-xs font-bold uppercase tracking-[0.12em] text-violet-100">
            {node.nodeType}
          </span>
          <span className="rounded-full border border-cyan-300/20 bg-cyan-400/10 px-3 py-1 text-xs font-semibold text-cyan-100">
            {statusText[node.progressStatus]}
          </span>
          {node.isLocked ? (
            <span className="inline-flex items-center gap-1 rounded-full border border-slate-400/20 bg-slate-400/10 px-3 py-1 text-xs font-semibold text-slate-300">
              <Lock size={12} />
              Locked
            </span>
          ) : null}
        </div>
      </div>

      <div className="min-h-0 flex-1 overflow-y-auto p-5">
        <section className="mb-5 rounded-2xl border border-white/[0.06] bg-white/[0.035] p-4">
          <div className="mb-2 flex items-center gap-2 text-sm font-bold text-slate-100">
            <BookOpen size={16} className="text-cyan-200" />
            Summary
          </div>
          <p className="text-sm leading-6 text-slate-400">{node.summary}</p>
        </section>

        <section className="mb-5 rounded-2xl border border-white/[0.06] bg-white/[0.035] p-4">
          <div className="mb-3 flex items-center gap-2 text-sm font-bold text-slate-100">
            <ClipboardList size={16} className="text-violet-200" />
            Checklist
          </div>
          <div className="space-y-2 text-sm text-slate-400">
            <p className="rounded-xl border border-dashed border-white/[0.08] bg-slate-900/50 px-3 py-2">
              Checklist content will appear here when this node includes learning content.
            </p>
          </div>
        </section>

        <section className="rounded-2xl border border-white/[0.06] bg-white/[0.035] p-4">
          <div className="mb-3 flex items-center gap-2 text-sm font-bold text-slate-100">
            <CheckCircle2 size={16} className="text-emerald-200" />
            Learning Resources
          </div>
          <p className="rounded-xl border border-dashed border-white/[0.08] bg-slate-900/50 px-3 py-2 text-sm text-slate-400">
            Resource links will appear here when they are available for this skill node.
          </p>
        </section>
      </div>
    </aside>
  );
}
