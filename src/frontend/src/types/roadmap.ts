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

export interface SkillNode {
  nodeId: string;
  roadmapId: string;
  title: string;
  slug: string;
  orderIndex: number;
  nodeType: 'LESSON' | 'PRACTICE' | 'PROJECT' | 'QUIZ' | 'CHALLENGE';
  contentJson: string | null;
  progressStatus: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
  isLocked: boolean;
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
