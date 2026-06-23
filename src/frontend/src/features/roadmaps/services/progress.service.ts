import api from "../../../services/axios";
import type { NodeProgressStatus } from "../types/roadmap";

export interface UserNodeProgress {
  progressId: string;
  userId: string;
  nodeId: string;
  roadmapId: string;
  status: NodeProgressStatus;
  unlockedAt: string | null;
  completedAt: string | null;
  lastAccessedAt: string | null;
}

export const progressService = {
  completeNode: async (nodeId: string): Promise<UserNodeProgress> => {
    const response = await api.post<UserNodeProgress>(`/users/me/progress/nodes/${nodeId}/complete`);
    return response.data;
  },
};
