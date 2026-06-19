import type { Edge, Node } from "@xyflow/react";
import type { RoadmapWithNodes, SkillNode } from "../../types/roadmap";

export type RoadmapCanvasProps = {
  roadmap: RoadmapWithNodes;
};

export type RoadmapSkillNodeData = {
  skillNode: SkillNode;
  title: string;
  orderIndex: number;
  nodeType: SkillNode["nodeType"];
  progressStatus: SkillNode["progressStatus"];
  isLocked: boolean;
  summary: string;
  isSearchMatch?: boolean;
  isSearchDimmed?: boolean;
} & Record<string, unknown>;

export type RoadmapSkillGraphNode = Node<RoadmapSkillNodeData, "roadmapSkillNode">;

export type RoadmapSkillGraphEdge = Edge<Record<string, never>, "smoothstep">;

export type RoadmapGraph = {
  nodes: RoadmapSkillGraphNode[];
  edges: RoadmapSkillGraphEdge[];
};
