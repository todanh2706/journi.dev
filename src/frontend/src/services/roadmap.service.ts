import api from "./axios";
import { type Roadmap } from "../types/roadmap";

export const roadmapService = {
    getRoadmaps: async (): Promise<Roadmap[]> => {
        const response = await api.get<Roadmap[]>("/roadmaps");
        return response.data;
    },

    getRoadmapById: async (id: string): Promise<Roadmap> => {
        const response = await api.get<Roadmap>(`/roadmaps/${id}`);
        return response.data;
    },
};
