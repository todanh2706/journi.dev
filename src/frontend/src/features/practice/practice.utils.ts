import type { CreateSubmissionInput, SubmissionStatus } from "./types";

export type SubmissionFieldErrors = Partial<Record<keyof CreateSubmissionInput, string>>;

const GITHUB_REPOSITORY = /^https:\/\/github\.com\/[A-Za-z0-9][A-Za-z0-9-]{0,38}\/[A-Za-z0-9._-]+(?:\.git)?\/?$/;
const BRANCH = /^[A-Za-z0-9._/-]{1,100}$/;
const FULL_SHA = /^[0-9a-fA-F]{40}$/;

export function validateSubmission(input: CreateSubmissionInput): SubmissionFieldErrors {
  const errors: SubmissionFieldErrors = {};
  const repositoryUrl = input.repositoryUrl.trim();
  const branch = input.branch.trim();
  const commitSha = input.commitSha.trim();

  if (!GITHUB_REPOSITORY.test(repositoryUrl)) {
    errors.repositoryUrl = "Use a public repository URL in the form https://github.com/owner/repository.";
  }
  if (!BRANCH.test(branch) || branch.startsWith(".") || branch.startsWith("/") || branch.endsWith(".") || branch.endsWith("/") || branch.includes("..") || branch.includes("//")) {
    errors.branch = "Enter a valid Git branch name such as main or feature/catalog.";
  }
  if (!FULL_SHA.test(commitSha)) {
    errors.commitSha = "Paste the full 40-character commit SHA, not a branch name or short SHA.";
  }
  return errors;
}

export const isActiveSubmission = (status: SubmissionStatus) => status === "SUBMITTED" || status === "EVALUATING";

export const isTerminalSubmission = (status: SubmissionStatus) => !isActiveSubmission(status);

export function pollingDelay(attempt: number): number {
  const delays = [1500, 2500, 4000, 7000, 10000];
  return delays[Math.min(Math.max(attempt, 0), delays.length - 1)];
}

export const canSubmitRevision = (submitting: boolean, status?: SubmissionStatus) =>
  !submitting && (!status || !isActiveSubmission(status));

export const canRetryEvaluation = (status: SubmissionStatus, retryable: boolean) =>
  status === "FAILED" && retryable;

export const shouldRefreshRoadmap = (status: SubmissionStatus, refreshedSubmissionId: string | null, submissionId: string) =>
  status === "PASSED" && refreshedSubmissionId !== submissionId;

export const practiceRoute = (roadmapId: string, nodeId: string) =>
  `/dashboard/roadmaps/${roadmapId}/nodes/${nodeId}/practice`;

export const roadmapRoute = (roadmapId?: string) =>
  roadmapId ? `/dashboard/roadmaps/${roadmapId}` : "/dashboard/roadmaps";

export function focusPracticeHeading(target: { focus: () => void } | null): void {
  target?.focus();
}

export const SUBMISSION_STATUS_TITLES: Record<SubmissionStatus, string> = {
  SUBMITTED: "Queued",
  EVALUATING: "Evaluating",
  PASSED: "Passed",
  NEEDS_CHANGES: "Needs changes",
  FAILED: "Infrastructure failure",
};
