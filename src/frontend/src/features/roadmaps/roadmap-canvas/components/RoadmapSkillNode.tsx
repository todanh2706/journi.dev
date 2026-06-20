import { memo } from "react";
import { BookOpen, Check, CheckSquare, Code2, FolderKanban, Lock, Unlock, Zap } from "lucide-react";
import { Handle, Position, type NodeProps } from "@xyflow/react";

import type { RoadmapSkillGraphNode, RoadmapSkillNodeData } from "../types";

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
    ? "border-line bg-shell text-muted"
    : isCompleted
      ? "border-success/35 bg-surface"
      : "border-gold/45 bg-surface-elevated";

  const state = data.isLocked
    ? { label: "Locked", Icon: Lock, className: "border-line bg-surface text-muted" }
    : isCompleted
      ? { label: "Completed", Icon: Check, className: "border-success/30 bg-success/10 text-green-200" }
      : isInProgress
        ? { label: "In progress", Icon: Unlock, className: "border-gold/35 bg-gold/10 text-gold-strong" }
        : { label: "Available", Icon: Unlock, className: "border-gold/35 bg-gold/10 text-gold-strong" };

  return (
    <div
      className={`relative w-[280px] rounded-2xl border p-4 text-left transition-colors ${cardState} ${
        selected ? "border-gold ring-2 ring-gold/35" : ""
      } ${data.isSearchMatch ? "outline-2 outline-offset-4 outline-warning" : ""} ${data.isSearchDimmed ? "opacity-45" : ""}`}
    >
      <Handle type="target" position={Position.Top} className="!h-2 !w-2 !border-0 !bg-gold-soft" />

      <div className="flex items-center justify-between gap-3">
        <span className="inline-flex max-w-[150px] items-center gap-1.5 rounded-lg border border-line bg-canvas/60 px-2 py-1 text-[10px] font-semibold uppercase tracking-[0.12em] text-muted">
          <TypeIcon aria-hidden="true" size={12} />
          {data.nodeType}
        </span>
        <span className={`inline-flex items-center gap-1 rounded-lg border px-2 py-1 text-[10px] font-semibold uppercase tracking-[0.1em] ${state.className}`}>
          <state.Icon aria-hidden="true" size={11} />
          {state.label}
        </span>
      </div>

      <p className="mt-4 text-[11px] font-semibold uppercase tracking-[0.15em] text-subtle">Step {data.orderIndex}</p>
      <h3 className="mt-1 line-clamp-2 text-base font-semibold leading-snug text-ink">{data.title}</h3>
      <p className="mt-2 line-clamp-1 text-xs text-muted">{data.summary}</p>
      {data.isSearchMatch ? <span className="sr-only">Search match</span> : null}

      <Handle type="source" position={Position.Bottom} className="!h-2 !w-2 !border-0 !bg-gold-soft" />
    </div>
  );
}

export const RoadmapSkillNode = memo(RoadmapSkillNodeComponent);
