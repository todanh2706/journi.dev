import { useEffect, useRef, useState } from "react";
import { AlertCircle, BookOpen, Check, CheckCircle2, Clock3, ExternalLink, Gauge, Lightbulb, Link2, ListChecks, LoaderCircle, Lock, X } from "lucide-react";

import type { RoadmapSkillNodeData } from "../types";
import { canShowLearningDetails, getCompletionActionPresentation, getFocusTrapTarget, NODE_STATUS_LABELS, normalizeLearningCollections, SAFE_EXTERNAL_LINK_PROPS, shouldCloseNodeDrawer, type CompletionRequestState } from "../utils/nodeDetailPresentation";

interface RoadmapNodeDrawerProps {
  node: RoadmapSkillNodeData | null;
  onClose: () => void;
  onCompleteNode: (nodeId: string) => Promise<void>;
}

export function RoadmapNodeDrawer({ node, onClose, onCompleteNode }: RoadmapNodeDrawerProps) {
  const panelRef = useRef<HTMLElement>(null);
  const closeButtonRef = useRef<HTMLButtonElement>(null);
  const [completionRequest, setCompletionRequest] = useState<{
    nodeId: string | null;
    state: CompletionRequestState;
  }>({ nodeId: null, state: "idle" });

  useEffect(() => {
    if (!node) return;
    closeButtonRef.current?.focus();

    const handleKeyDown = (event: KeyboardEvent) => {
      if (shouldCloseNodeDrawer(event.key)) onClose();

      if (event.key !== "Tab" || !panelRef.current) return;
      const focusable = Array.from(panelRef.current.querySelectorAll<HTMLElement>('button:not([disabled]), [href], [tabindex]:not([tabindex="-1"])'));
      const focusTarget = getFocusTrapTarget(focusable, document.activeElement as HTMLElement | null, event.shiftKey);
      if (focusTarget) {
        event.preventDefault();
        focusTarget.focus();
      }
    };

    document.addEventListener("keydown", handleKeyDown);
    return () => document.removeEventListener("keydown", handleKeyDown);
  }, [node, onClose]);

  if (!node) return null;

  const isLocked = !canShowLearningDetails(node.skillNode);
  const { skillNode } = node;
  const { checklist, learningResources } = normalizeLearningCollections(skillNode);
  const completionState = completionRequest.nodeId === skillNode.nodeId ? completionRequest.state : "idle";
  const completionAction = getCompletionActionPresentation(skillNode, completionState);

  const handleComplete = async () => {
    if (!completionAction.visible || completionAction.disabled) return;

    setCompletionRequest({ nodeId: skillNode.nodeId, state: "pending" });
    try {
      await onCompleteNode(skillNode.nodeId);
      setCompletionRequest({ nodeId: skillNode.nodeId, state: "idle" });
    } catch {
      setCompletionRequest({ nodeId: skillNode.nodeId, state: "error" });
    }
  };

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
            <span className={`rounded-lg border px-2.5 py-1 text-xs font-medium ${isLocked ? "border-line bg-surface text-muted" : node.progressStatus === "COMPLETED" ? "border-success/30 bg-success/10 text-green-200" : "border-gold/30 bg-gold/10 text-gold-strong"}`}>
              {NODE_STATUS_LABELS[node.progressStatus]}
            </span>
          </div>
        </header>

        <div className="min-h-0 flex-1 space-y-4 overflow-y-auto p-5">
          {isLocked ? (
            <section className="rounded-xl border border-line bg-surface p-4">
              <div className="flex items-center gap-2 text-sm font-semibold text-ink"><Lock aria-hidden="true" size={16} className="text-muted" /> Not available yet</div>
              <p className="mt-2 text-sm leading-6 text-muted">Complete the prerequisite skills before starting this node.</p>
            </section>
          ) : (
            <>
              <section className="rounded-xl border border-line bg-surface p-4">
                <div className="flex items-center gap-2 text-sm font-semibold text-ink"><BookOpen aria-hidden="true" size={16} className="text-gold" /> Summary</div>
                <p className="mt-2 text-sm leading-6 text-muted">{skillNode.summary ?? "No summary has been published for this skill yet."}</p>

                <div className="mt-4 grid grid-cols-2 gap-2">
                  <div className="rounded-lg border border-line bg-canvas/45 p-3">
                    <div className="flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-[0.12em] text-subtle"><Gauge aria-hidden="true" size={13} /> Level</div>
                    <p className="mt-1.5 text-sm font-medium capitalize text-ink">{skillNode.level?.toLowerCase() ?? "Not specified"}</p>
                  </div>
                  <div className="rounded-lg border border-line bg-canvas/45 p-3">
                    <div className="flex items-center gap-1.5 text-[10px] font-semibold uppercase tracking-[0.12em] text-subtle"><Clock3 aria-hidden="true" size={13} /> Estimate</div>
                    <p className="mt-1.5 text-sm font-medium text-ink">{skillNode.estimatedHours ? `${skillNode.estimatedHours} hours` : "Not specified"}</p>
                  </div>
                </div>

                {skillNode.note ? (
                  <div className="mt-3 rounded-lg border border-gold/20 bg-gold/5 p-3">
                    <div className="flex items-center gap-1.5 text-xs font-semibold text-gold-strong"><Lightbulb aria-hidden="true" size={14} /> Learning note</div>
                    <p className="mt-1.5 text-sm leading-6 text-muted">{skillNode.note}</p>
                  </div>
                ) : null}
              </section>

              <section className="rounded-xl border border-line bg-surface p-4">
                <div className="flex items-center gap-2 text-sm font-semibold text-ink"><ListChecks aria-hidden="true" size={16} className="text-gold" /> Checklist</div>
                {checklist.length > 0 ? (
                  <ul className="mt-3 space-y-2.5">
                    {checklist.map((item, index) => (
                      <li key={`${index}-${item}`} className="flex gap-2.5 rounded-lg border border-line bg-canvas/45 p-3 text-sm leading-5 text-muted">
                        <span aria-hidden="true" className="mt-0.5 inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-md border border-gold/35 bg-gold/10 text-gold-strong"><Check size={12} /></span>
                        <span>{item}</span>
                      </li>
                    ))}
                  </ul>
                ) : <p className="mt-2 text-sm leading-6 text-muted">No checklist has been published for this skill yet.</p>}
              </section>

              <section className="rounded-xl border border-line bg-surface p-4">
                <div className="flex items-center gap-2 text-sm font-semibold text-ink"><Link2 aria-hidden="true" size={16} className="text-success" /> Learning resources</div>
                {learningResources.length > 0 ? (
                  <ul className="mt-3 space-y-2.5">
                    {learningResources.map((resource) => (
                      <li key={`${resource.sourceUrl}-${resource.title}`}>
                        <a
                          href={resource.sourceUrl}
                          {...SAFE_EXTERNAL_LINK_PROPS}
                          className="group block rounded-lg border border-line bg-canvas/45 p-3 transition-colors hover:border-line-strong hover:bg-surface-elevated"
                        >
                          <span className="flex items-start justify-between gap-3">
                            <span className="text-sm font-medium leading-5 text-ink group-hover:text-gold-strong">{resource.title}</span>
                            <ExternalLink aria-hidden="true" size={14} className="mt-0.5 shrink-0 text-subtle group-hover:text-gold" />
                          </span>
                          <span className="mt-1.5 block text-[10px] font-semibold uppercase tracking-[0.12em] text-gold">{resource.sourceType}</span>
                          {resource.description ? <span className="mt-1.5 block text-xs leading-5 text-muted">{resource.description}</span> : null}
                        </a>
                      </li>
                    ))}
                  </ul>
                ) : <p className="mt-2 text-sm leading-6 text-muted">No learning resources have been published for this skill yet.</p>}
              </section>
            </>
          )}
        </div>

        {!isLocked && skillNode.nodeType === "LESSON" ? (
          <footer className="border-t border-line bg-surface/70 p-5">
            {skillNode.progressStatus === "COMPLETED" ? (
              <div className="flex items-start gap-3 rounded-xl border border-success/30 bg-success/10 p-3.5 text-green-100" aria-live="polite">
                <CheckCircle2 aria-hidden="true" size={18} className="mt-0.5 shrink-0 text-success" />
                <div>
                  <p className="text-sm font-semibold">Lesson completed</p>
                  <p className="mt-1 text-xs leading-5 text-green-100/70">Your roadmap progress is up to date.</p>
                </div>
              </div>
            ) : completionAction.visible ? (
              <div>
                {completionState === "error" ? (
                  <div role="alert" className="mb-3 flex gap-2 rounded-lg border border-danger/30 bg-danger/10 p-3 text-xs leading-5 text-red-100">
                    <AlertCircle aria-hidden="true" size={15} className="mt-0.5 shrink-0 text-danger" />
                    Completion could not be saved. Check your connection and try again.
                  </div>
                ) : null}
                <button
                  type="button"
                  onClick={() => void handleComplete()}
                  disabled={completionAction.disabled}
                  className="primary-button w-full disabled:cursor-wait disabled:opacity-60"
                >
                  {completionState === "pending" ? <LoaderCircle aria-hidden="true" size={16} className="animate-spin motion-reduce:animate-none" /> : <CheckCircle2 aria-hidden="true" size={16} />}
                  {completionAction.label}
                </button>
                <p className="mt-2 text-center text-xs leading-5 text-subtle">Confirm after you have finished the reading and checklist.</p>
              </div>
            ) : null}
          </footer>
        ) : null}
      </aside>
    </div>
  );
}
