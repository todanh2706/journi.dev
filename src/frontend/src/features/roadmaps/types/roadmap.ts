export interface Roadmap {
  roadmapId: string;
  title: string;
  slug: string;
  description: string;
  visibility: string;
  isDynamic: boolean;
  createdBy: string | null;
  updatedBy: string | null;
  createdAt: string;
  updatedAt: string | null;
  deletedAt: string | null;
}

export type NodeProgressStatus = 'LOCKED' | 'AVAILABLE' | 'IN_PROGRESS' | 'COMPLETED';

export interface LearningResource {
  title: string;
  sourceType: string;
  sourceUrl: string;
  description: string;
}

export interface SkillNode {
  nodeId: string;
  roadmapId: string;
  title: string;
  slug: string;
  orderIndex: number;
  nodeType: 'LESSON' | 'PRACTICE' | 'PROJECT' | 'QUIZ' | 'CHALLENGE';
  contentJson: string | null;
  summary: string | null;
  level: string | null;
  estimatedHours: number | null;
  note: string | null;
  checklist: string[] | null;
  learningResources: LearningResource[] | null;
  progressStatus: NodeProgressStatus;
  isLocked: boolean;
  hasRequiredChallenge: boolean;
  practiceSubmissionEnabled: boolean;
  createdBy: string | null;
  updatedBy: string | null;
  createdAt: string;
  updatedAt: string | null;
  deletedAt: string | null;
}

export interface RoadmapWithNodes extends Roadmap {
  nodes: SkillNode[];
}

export interface RoadmapResponse {
  data: Roadmap[];
  message?: string;
}
