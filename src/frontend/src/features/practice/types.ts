export type SubmissionStatus = "SUBMITTED" | "EVALUATING" | "PASSED" | "NEEDS_CHANGES" | "FAILED";

export interface SubmissionSummary {
  submissionId: string;
  attemptNumber: number;
  status: SubmissionStatus;
  score: number | null;
  submittedAt: string;
  completedAt: string | null;
}

export interface PracticeChallenge {
  challengeId: string;
  nodeId: string;
  title: string;
  description: string;
  difficulty: string;
  instructions: string;
  acceptanceCriteria: string[];
  hints: string[];
  expectedArtifacts: string[];
  starterRepositoryUrl: string;
  maxScore: number;
  passingScore: number;
  timeoutSeconds: number;
  required: boolean;
  submissionEnabled: boolean;
  progressStatus: "AVAILABLE" | "IN_PROGRESS" | "COMPLETED";
  currentSubmission: SubmissionSummary | null;
}

export interface CriterionFeedback {
  criterion: string;
  passed: boolean;
  message: string;
}

export interface PracticeSubmission {
  submissionId: string;
  challengeId: string;
  repositoryUrl: string;
  branch: string;
  commitSha: string;
  attemptNumber: number;
  status: SubmissionStatus;
  score: number | null;
  resultSummary: string | null;
  feedback: CriterionFeedback[];
  outputExcerpt: string | null;
  retryable: boolean;
  submittedAt: string;
  evaluationStartedAt: string | null;
  completedAt: string | null;
}

export interface CreateSubmissionInput {
  repositoryUrl: string;
  branch: string;
  commitSha: string;
}
