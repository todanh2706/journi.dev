import { memo } from "react";
import { BookOpen, Check, CheckSquare, Code2, FolderKanban, Lock, Sparkles, Zap } from "lucide-react";
import { Handle, Position, type NodeProps } from "@xyflow/react";
import type { RoadmapSkillGraphNode, RoadmapSkillNodeData } from "../types";

const nodeTypeStyles: Record<RoadmapSkillNodeData["nodeType"], string> = {
  LESSON: "border-sky-400/20 bg-sky-400/10 text-sky-200",
  PRACTICE: "border-violet-400/20 bg-violet-400/10 text-violet-200",
  PROJECT: "border-amber-300/20 bg-amber-300/10 text-amber-100",
  QUIZ: "border-emerald-300/20 bg-emerald-300/10 text-emerald-100",
  CHALLENGE: "border-rose-300/20 bg-rose-300/10 text-rose-100",
};

const nodeTypeIcons: Record<RoadmapSkillNodeData["nodeType"], typeof BookOpen> = {
  LESSON: BookOpen,
  PRACTICE: Code2,
  PROJECT: FolderKanban,
  QUIZ: CheckSquare,
  CHALLENGE: Zap,
};

function RoadmapSkillNodeComponent({ data, selected }: NodeProps<RoadmapSkillGraphNode>) {
  const isCompleted = data.progressStatus === "COMPLETED";
  const isInProgress = data.progressStatus === "IN_PROGRESS";
  const TypeIcon = nodeTypeIcons[data.nodeType];

  const cardState = data.isLocked
    ? "border-white/[0.06] bg-slate-950/80 opacity-75"
    : isCompleted
      ? "border-emerald-400/30 bg-slate-950/90 shadow-[0_0_24px_rgba(16,185,129,0.08)]"
      : isInProgress
        ? "border-violet-400/40 bg-slate-950/95 shadow-[0_0_28px_rgba(124,58,237,0.16)]"
        : "border-white/[0.08] bg-slate-950/90";

  const searchState = data.isSearchMatch
    ? "ring-2 ring-cyan-300/70"
    : data.isSearchDimmed
      ? "opacity-45"
      : "";

  return (
    <div
      className={`group relative w-[280px] rounded-2xl border p-4 text-left text-slate-100 transition-colors duration-200 hover:border-violet-300/45 ${cardState} ${searchState} ${
        selected ? "ring-2 ring-violet-300/70" : ""
      }`}
    >
      <Handle
        type="target"
        position={Position.Top}
        className="!h-2 !w-2 !border-0 !bg-violet-300/40"
      />
      <div className="pointer-events-none absolute inset-0 rounded-2xl bg-[radial-gradient(circle_at_top_right,rgba(129,140,248,0.18),transparent_34%),linear-gradient(135deg,rgba(255,255,255,0.08),transparent_42%)] opacity-80" />
      {isInProgress ? (
        <div className="pointer-events-none absolute inset-0 rounded-2xl ring-1 ring-inset ring-cyan-300/20" />
      ) : null}

      <div className="relative z-10">
        <div className="mb-3 flex items-center justify-between gap-3">
          <span
            className={`inline-flex max-w-[150px] items-center gap-1.5 rounded-full border px-2.5 py-1 text-[10px] font-bold uppercase tracking-[0.12em] ${nodeTypeStyles[data.nodeType]}`}
          >
            <TypeIcon size={12} />
            {data.nodeType}
          </span>

          {data.isLocked ? (
            <span className="inline-flex items-center gap-1 rounded-full border border-slate-500/20 bg-slate-500/10 px-2 py-1 text-[10px] font-bold uppercase tracking-[0.12em] text-slate-400">
              <Lock size={11} />
              Locked
            </span>
          ) : isCompleted ? (
            <span className="inline-flex items-center gap-1 rounded-full border border-emerald-300/20 bg-emerald-400/10 px-2 py-1 text-[10px] font-bold uppercase tracking-[0.12em] text-emerald-200">
              <Check size={11} />
              Done
            </span>
          ) : isInProgress ? (
            <span className="inline-flex items-center gap-1 rounded-full border border-violet-300/20 bg-violet-400/10 px-2 py-1 text-[10px] font-bold uppercase tracking-[0.12em] text-violet-100">
              <Sparkles size={11} />
              Active
            </span>
          ) : null}
        </div>

        <div className="mb-1 text-[11px] font-semibold uppercase tracking-[0.16em] text-slate-500">
          Step {data.orderIndex}
        </div>
        <h3 className="line-clamp-2 text-base font-bold leading-snug text-slate-50 transition group-hover:text-violet-100">
          {data.title}
        </h3>
        <p className="mt-2 line-clamp-1 text-xs text-slate-400">{data.summary}</p>
      </div>

      <Handle
        type="source"
        position={Position.Bottom}
        className="!h-2 !w-2 !border-0 !bg-violet-300/40"
      />
    </div>
  );
}

export const RoadmapSkillNode = memo(RoadmapSkillNodeComponent);
