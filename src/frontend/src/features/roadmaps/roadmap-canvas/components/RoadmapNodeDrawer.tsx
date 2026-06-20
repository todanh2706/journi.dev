import { useEffect, useRef } from "react";
import { BookOpen, CheckCircle2, ClipboardList, Lock, X } from "lucide-react";

import type { RoadmapSkillNodeData } from "../types";

interface RoadmapNodeDrawerProps {
  node: RoadmapSkillNodeData | null;
  onClose: () => void;
}

const statusText: Record<RoadmapSkillNodeData["progressStatus"], string> = {
  NOT_STARTED: "Available",
  IN_PROGRESS: "In progress",
  COMPLETED: "Completed",
};

export function RoadmapNodeDrawer({ node, onClose }: RoadmapNodeDrawerProps) {
  const panelRef = useRef<HTMLElement>(null);
  const closeButtonRef = useRef<HTMLButtonElement>(null);

  useEffect(() => {
    if (!node) return;
    closeButtonRef.current?.focus();

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Escape") onClose();

      if (event.key !== "Tab" || !panelRef.current) return;
      const focusable = Array.from(panelRef.current.querySelectorAll<HTMLElement>('button:not([disabled]), [href], [tabindex]:not([tabindex="-1"])'));
      if (focusable.length === 0) return;
      const first = focusable[0];
      const last = focusable[focusable.length - 1];
      if (event.shiftKey && document.activeElement === first) {
        event.preventDefault();
        last.focus();
      } else if (!event.shiftKey && document.activeElement === last) {
        event.preventDefault();
        first.focus();
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [node, onClose]);

  if (!node) return null;

  return (
    <div className="absolute inset-0 z-30 flex items-end justify-end bg-black/55 p-0 sm:items-stretch sm:p-3" onMouseDown={(event) => { if (event.target === event.currentTarget) onClose(); }}>
      <aside
        ref={panelRef}
        role="dialog"
        aria-modal="true"
        aria-labelledby="node-detail-title"
        className="flex max-h-[82%] w-full flex-col overflow-hidden rounded-t-2xl border border-line bg-shell shadow-2xl shadow-black/55 sm:max-h-none sm:w-[min(24rem,calc(100%-1.5rem))] sm:rounded-2xl"
      >
        <header className="border-b border-line p-5">
          <div className="flex items-start justify-between gap-4">
            <div>
              <p className="eyebrow">Step {node.orderIndex}</p>
              <h2 id="node-detail-title" className="mt-2 text-xl font-semibold leading-tight text-ink">{node.title}</h2>
            </div>
            <button ref={closeButtonRef} type="button" onClick={onClose} className="icon-button" aria-label="Close node details">
              <X aria-hidden="true" size={18} />
            </button>
          </div>

          <div className="mt-4 flex flex-wrap gap-2">
            <span className="rounded-lg border border-line bg-surface px-2.5 py-1 text-xs font-semibold uppercase tracking-[0.1em] text-muted">{node.nodeType}</span>
            <span className="rounded-lg border border-gold/30 bg-gold/10 px-2.5 py-1 text-xs font-medium text-gold-strong">{statusText[node.progressStatus]}</span>
            {node.isLocked ? <span className="inline-flex items-center gap-1 rounded-lg border border-line bg-surface px-2.5 py-1 text-xs font-medium text-muted"><Lock aria-hidden="true" size={12} /> Locked</span> : null}
          </div>
        </header>

        <div className="min-h-0 flex-1 space-y-4 overflow-y-auto p-5">
          {node.isLocked ? (
            <section className="rounded-xl border border-line bg-surface p-4">
              <div className="flex items-center gap-2 text-sm font-semibold text-ink"><Lock aria-hidden="true" size={16} className="text-muted" /> Not available yet</div>
              <p className="mt-2 text-sm leading-6 text-muted">Complete the prerequisite skills before starting this node.</p>
            </section>
          ) : null}

          <section className="rounded-xl border border-line bg-surface p-4">
            <div className="flex items-center gap-2 text-sm font-semibold text-ink"><BookOpen aria-hidden="true" size={16} className="text-gold" /> Summary</div>
            <p className="mt-2 text-sm leading-6 text-muted">{node.summary}</p>
          </section>

          <section className="rounded-xl border border-line bg-surface p-4">
            <div className="flex items-center gap-2 text-sm font-semibold text-ink"><ClipboardList aria-hidden="true" size={16} className="text-gold" /> Checklist</div>
            <p className="mt-2 text-sm leading-6 text-muted">No checklist has been published for this skill yet.</p>
          </section>

          <section className="rounded-xl border border-line bg-surface p-4">
            <div className="flex items-center gap-2 text-sm font-semibold text-ink"><CheckCircle2 aria-hidden="true" size={16} className="text-success" /> Learning resources</div>
            <p className="mt-2 text-sm leading-6 text-muted">No learning resources have been published for this skill yet.</p>
          </section>
        </div>
      </aside>
    </div>
  );
}
