import api from "../../../services/axios";
import { type Roadmap, type SkillNode, type RoadmapWithNodes } from "../types/roadmap";

export const roadmapService = {
    getRoadmaps: async (): Promise<Roadmap[]> => {
        const response = await api.get<Roadmap[]>("/roadmaps");
        return response.data;
    },

    getRoadmapById: async (id: string): Promise<Roadmap> => {
        const response = await api.get<Roadmap>(`/roadmaps/${id}`);
        return response.data;
    },

    getRoadmapNodes: async (id: string): Promise<SkillNode[]> => {
        const response = await api.get<SkillNode[]>(`/roadmaps/${id}/nodes`);
        return response.data;
    },

    getRoadmapWithNodes: async (id: string): Promise<RoadmapWithNodes> => {
        const [roadmap, nodes] = await Promise.all([
            roadmapService.getRoadmapById(id),
            roadmapService.getRoadmapNodes(id)
        ]);
        
        return {
            ...roadmap,
            nodes
        };
    }
};
