import assert from "node:assert/strict";
import test from "node:test";

import {
  canManuallyCompleteLesson,
  canShowLearningDetails,
  findSelectedNodeById,
  getCompletionActionPresentation,
  getFocusTrapTarget,
  NODE_STATUS_LABELS,
  normalizeLearningCollections,
  SAFE_EXTERNAL_LINK_PROPS,
  shouldCloseNodeDrawer,
} from "../src/features/roadmaps/roadmap-canvas/utils/nodeDetailPresentation.ts";
import { completeRoadmapNode } from "../src/features/roadmaps/utils/completeRoadmapNode.ts";

test("maps every backend progress status to an explicit label", () => {
  assert.deepEqual(NODE_STATUS_LABELS, {
    LOCKED: "Locked",
    AVAILABLE: "Available",
    IN_PROGRESS: "In progress",
    COMPLETED: "Completed",
  });
});

test("shows learning details for every unlocked progress status", () => {
  for (const progressStatus of ["AVAILABLE", "IN_PROGRESS", "COMPLETED"]) {
    assert.equal(canShowLearningDetails({ isLocked: false, progressStatus }), true);
  }
});

test("suppresses learning details when either lock signal is present", () => {
  assert.equal(canShowLearningDetails({ isLocked: true, progressStatus: "AVAILABLE" }), false);
  assert.equal(canShowLearningDetails({ isLocked: false, progressStatus: "LOCKED" }), false);
});

test("uses safe attributes for external learning resources", () => {
  assert.deepEqual(SAFE_EXTERNAL_LINK_PROPS, {
    target: "_blank",
    rel: "noreferrer",
  });
});

test("closes the drawer only for Escape", () => {
  assert.equal(shouldCloseNodeDrawer("Escape"), true);
  assert.equal(shouldCloseNodeDrawer("Enter"), false);
  assert.equal(shouldCloseNodeDrawer("Tab"), false);
});

test("wraps keyboard focus at both ends of the drawer", () => {
  const first = { id: "close" };
  const middle = { id: "resource" };
  const last = { id: "last-resource" };
  const focusable = [first, middle, last];

  assert.equal(getFocusTrapTarget(focusable, first, true), last);
  assert.equal(getFocusTrapTarget(focusable, last, false), first);
  assert.equal(getFocusTrapTarget(focusable, middle, false), null);
  assert.equal(getFocusTrapTarget([], null, false), null);
});

test("normalizes partial unlocked learning data to empty collections", () => {
  assert.deepEqual(normalizeLearningCollections({ checklist: null }), {
    checklist: [],
    learningResources: [],
  });
  assert.deepEqual(normalizeLearningCollections({ learningResources: [{ title: "Java Docs" }] }), {
    checklist: [],
    learningResources: [{ title: "Java Docs" }],
  });
});

test("shows manual completion only for unlocked, incomplete lessons", () => {
  assert.equal(canManuallyCompleteLesson({ nodeType: "LESSON", isLocked: false, progressStatus: "AVAILABLE" }), true);
  assert.equal(canManuallyCompleteLesson({ nodeType: "LESSON", isLocked: false, progressStatus: "IN_PROGRESS" }), true);
  assert.equal(canManuallyCompleteLesson({ nodeType: "LESSON", isLocked: false, progressStatus: "COMPLETED" }), false);
  assert.equal(canManuallyCompleteLesson({ nodeType: "LESSON", isLocked: true, progressStatus: "LOCKED" }), false);
  assert.equal(canManuallyCompleteLesson({ nodeType: "PRACTICE", isLocked: false, progressStatus: "AVAILABLE" }), false);
});

test("disables duplicate completion while pending and allows retry after an error", () => {
  const lesson = { nodeType: "LESSON", isLocked: false, progressStatus: "AVAILABLE" };

  assert.deepEqual(getCompletionActionPresentation(lesson, "pending"), {
    visible: true,
    disabled: true,
    label: "Completing…",
  });
  assert.deepEqual(getCompletionActionPresentation(lesson, "error"), {
    visible: true,
    disabled: false,
    label: "Try again",
  });
});

test("refreshes roadmap nodes only after completion succeeds", async () => {
  const calls = [];
  await completeRoadmapNode(
    "node-2",
    async (nodeId) => calls.push(`complete:${nodeId}`),
    async () => calls.push("refresh"),
  );
  assert.deepEqual(calls, ["complete:node-2", "refresh"]);

  await assert.rejects(() => completeRoadmapNode(
    "node-2",
    async () => { throw new Error("request failed"); },
    async () => calls.push("unexpected-refresh"),
  ));
  assert.equal(calls.includes("unexpected-refresh"), false);
});

test("preserves the selected node identity when refreshed graph data replaces its status", () => {
  const selectedNodeId = "node-2";
  const refreshedNodes = [{ id: selectedNodeId, data: { progressStatus: "COMPLETED" } }];

  assert.deepEqual(findSelectedNodeById(refreshedNodes, selectedNodeId), { progressStatus: "COMPLETED" });
  assert.equal(findSelectedNodeById(refreshedNodes, null), null);
});
