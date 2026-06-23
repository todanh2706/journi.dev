import type { NodeProgressStatus } from "../../types/roadmap";

type LearningDetailAccess = {
  isLocked: boolean;
  progressStatus: NodeProgressStatus;
};

type LessonCompletionAccess = LearningDetailAccess & {
  nodeType: string;
};

export type CompletionRequestState = "idle" | "pending" | "error";

export const NODE_STATUS_LABELS: Record<NodeProgressStatus, string> = {
  LOCKED: "Locked",
  AVAILABLE: "Available",
  IN_PROGRESS: "In progress",
  COMPLETED: "Completed",
};

export const SAFE_EXTERNAL_LINK_PROPS = {
  target: "_blank",
  rel: "noreferrer",
} as const;

export const canShowLearningDetails = (node: LearningDetailAccess) =>
  !node.isLocked && node.progressStatus !== "LOCKED";

export const canManuallyCompleteLesson = (node: LessonCompletionAccess) =>
  node.nodeType === "LESSON"
  && canShowLearningDetails(node)
  && node.progressStatus !== "COMPLETED";

export const getCompletionActionPresentation = (
  node: LessonCompletionAccess,
  requestState: CompletionRequestState,
) => ({
  visible: canManuallyCompleteLesson(node),
  disabled: requestState === "pending",
  label: requestState === "pending"
    ? "Completing…"
    : requestState === "error"
      ? "Try again"
      : "Mark as complete",
});

export const findSelectedNodeById = <NodeData>(
  nodes: readonly { id: string; data: NodeData }[],
  selectedNodeId: string | null,
): NodeData | null => nodes.find((node) => node.id === selectedNodeId)?.data ?? null;

export const shouldCloseNodeDrawer = (key: string) => key === "Escape";

export const getFocusTrapTarget = <T>(
  focusableElements: readonly T[],
  activeElement: T | null,
  shiftKey: boolean,
): T | null => {
  if (focusableElements.length === 0) return null;

  const first = focusableElements[0];
  const last = focusableElements[focusableElements.length - 1];

  if (shiftKey && activeElement === first) return last;
  if (!shiftKey && activeElement === last) return first;
  return null;
};

export const normalizeLearningCollections = <ChecklistItem, Resource>(details: {
  checklist?: readonly ChecklistItem[] | null;
  learningResources?: readonly Resource[] | null;
}) => ({
  checklist: details.checklist ?? [],
  learningResources: details.learningResources ?? [],
});
