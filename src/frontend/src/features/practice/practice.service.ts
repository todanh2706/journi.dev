import api from "../../services/axios";
import type { CreateSubmissionInput, PracticeChallenge, PracticeSubmission } from "./types";

export const practiceService = {
  getChallenge: async (nodeId: string): Promise<PracticeChallenge> => {
    const response = await api.get<PracticeChallenge>(`/skill-nodes/${nodeId}/challenge`);
    return response.data;
  },

  createSubmission: async (challengeId: string, input: CreateSubmissionInput): Promise<PracticeSubmission> => {
    const response = await api.post<PracticeSubmission>(`/users/me/challenges/${challengeId}/submissions`, input);
    return response.data;
  },

  getHistory: async (challengeId: string): Promise<PracticeSubmission[]> => {
    const response = await api.get<PracticeSubmission[]>(`/users/me/challenges/${challengeId}/submissions`);
    return response.data;
  },

  getSubmission: async (submissionId: string): Promise<PracticeSubmission> => {
    const response = await api.get<PracticeSubmission>(`/users/me/submissions/${submissionId}`);
    return response.data;
  },

  retrySubmission: async (submissionId: string): Promise<PracticeSubmission> => {
    const response = await api.post<PracticeSubmission>(`/users/me/submissions/${submissionId}/retry`);
    return response.data;
  },
};
