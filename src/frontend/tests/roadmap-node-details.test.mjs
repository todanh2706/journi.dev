import assert from "node:assert/strict";
import test from "node:test";

import {
  canManuallyCompleteLesson,
  canShowLearningDetails,
  findSelectedNodeById,
  getCompletionActionPresentation,
  getFocusTrapTarget,
  getPracticeAction,
  NODE_STATUS_LABELS,
  normalizeLearningCollections,
  SAFE_EXTERNAL_LINK_PROPS,
  shouldCloseNodeDrawer,
} from "../src/features/roadmaps/roadmap-canvas/utils/nodeDetailPresentation.ts";
import { completeRoadmapNode } from "../src/features/roadmaps/utils/completeRoadmapNode.ts";
import { canRetryEvaluation, canSubmitRevision, focusPracticeHeading, isActiveSubmission, isTerminalSubmission, pollingDelay, practiceRoute, roadmapRoute, shouldRefreshRoadmap, SUBMISSION_STATUS_TITLES, validateSubmission } from "../src/features/practice/practice.utils.ts";

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

test("keeps unlocked practice briefs accessible when automated submission is disabled", () => {
  const base = { nodeType: "PRACTICE", isLocked: false, progressStatus: "AVAILABLE", hasRequiredChallenge: true, practiceSubmissionEnabled: true };
  assert.equal(getPracticeAction(base), "start");
  assert.equal(getPracticeAction({ ...base, progressStatus: "COMPLETED" }), "history");
  assert.equal(getPracticeAction({ ...base, practiceSubmissionEnabled: false }), "brief");
  assert.equal(getPracticeAction({ ...base, isLocked: true, progressStatus: "LOCKED" }), "hidden");
  assert.equal(getPracticeAction({ ...base, nodeType: "LESSON" }), "hidden");
  assert.equal(getPracticeAction({ ...base, hasRequiredChallenge: false }), "hidden");
});

test("validates repository, branch, and immutable commit fields before submission", () => {
  assert.deepEqual(validateSubmission({ repositoryUrl: "https://github.com/alex/catalog", branch: "feature/books", commitSha: "a".repeat(40) }), {});
  assert.deepEqual(Object.keys(validateSubmission({ repositoryUrl: "http://localhost/repo", branch: "../main", commitSha: "abc" })).sort(), ["branch", "commitSha", "repositoryUrl"]);
});

test("defines bounded polling and terminal lifecycle semantics", () => {
  assert.equal(isActiveSubmission("SUBMITTED"), true);
  assert.equal(isActiveSubmission("EVALUATING"), true);
  for (const status of ["PASSED", "NEEDS_CHANGES", "FAILED"]) assert.equal(isTerminalSubmission(status), true);
  assert.deepEqual([0, 1, 2, 3, 4, 20].map(pollingDelay), [1500, 2500, 4000, 7000, 10000, 10000]);
});

test("prevents duplicate submission and applies infrastructure-only retry semantics", () => {
  assert.equal(canSubmitRevision(false), true);
  assert.equal(canSubmitRevision(true), false);
  assert.equal(canSubmitRevision(false, "SUBMITTED"), false);
  assert.equal(canSubmitRevision(false, "EVALUATING"), false);
  assert.equal(canSubmitRevision(false, "NEEDS_CHANGES"), true);
  assert.equal(canRetryEvaluation("FAILED", true), true);
  assert.equal(canRetryEvaluation("NEEDS_CHANGES", true), false);
  assert.equal(canRetryEvaluation("FAILED", false), false);
});

test("defines distinct safe learner-facing status presentations", () => {
  assert.deepEqual(SUBMISSION_STATUS_TITLES, {
    SUBMITTED: "Queued",
    EVALUATING: "Evaluating",
    PASSED: "Passed",
    NEEDS_CHANGES: "Needs changes",
    FAILED: "Infrastructure failure",
  });
});

test("refreshes once on pass and builds stable practice navigation paths", () => {
  assert.equal(shouldRefreshRoadmap("PASSED", null, "submission-1"), true);
  assert.equal(shouldRefreshRoadmap("PASSED", "submission-1", "submission-1"), false);
  assert.equal(shouldRefreshRoadmap("NEEDS_CHANGES", null, "submission-1"), false);
  assert.equal(practiceRoute("roadmap-1", "node-4"), "/dashboard/roadmaps/roadmap-1/nodes/node-4/practice");
  assert.equal(roadmapRoute("roadmap-1"), "/dashboard/roadmaps/roadmap-1");
  assert.equal(roadmapRoute(), "/dashboard/roadmaps");
});

test("moves focus to the practice heading after navigation data loads", () => {
  let focused = false;
  focusPracticeHeading({ focus: () => { focused = true; } });
  assert.equal(focused, true);
  assert.doesNotThrow(() => focusPracticeHeading(null));
});
