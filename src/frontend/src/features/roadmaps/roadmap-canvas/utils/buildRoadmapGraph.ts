import { MarkerType, Position } from "@xyflow/react";
import type { SkillNode } from "../../types/roadmap";
import type { RoadmapGraph, RoadmapSkillGraphEdge, RoadmapSkillGraphNode } from "../types";

const NODE_WIDTH = 280;
const NODE_HEIGHT = 138;

const slugToSummary = (slug: string) =>
  slug
    .split("-")
    .filter(Boolean)
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(" ");

export const buildRoadmapGraph = (skillNodes: SkillNode[]): RoadmapGraph => {
  const sortedNodes = [...skillNodes].sort((a, b) => a.orderIndex - b.orderIndex);

  const nodes: RoadmapSkillGraphNode[] = sortedNodes.map((skillNode) => ({
    id: skillNode.nodeId,
    type: "roadmapSkillNode",
    position: { x: 0, y: 0 },
    width: NODE_WIDTH,
    height: NODE_HEIGHT,
    initialWidth: NODE_WIDTH,
    initialHeight: NODE_HEIGHT,
    targetPosition: Position.Top,
    sourcePosition: Position.Bottom,
    ariaLabel: `Step ${skillNode.orderIndex}: ${skillNode.title}. ${skillNode.isLocked ? "Locked" : skillNode.progressStatus === "COMPLETED" ? "Completed" : skillNode.progressStatus === "IN_PROGRESS" ? "In progress" : "Available"}. Press Enter to inspect.`,
    data: {
      skillNode,
      title: skillNode.title,
      orderIndex: skillNode.orderIndex,
      nodeType: skillNode.nodeType,
      progressStatus: skillNode.progressStatus,
      isLocked: skillNode.isLocked,
      summary: slugToSummary(skillNode.slug),
    },
  }));

  const edges: RoadmapSkillGraphEdge[] = sortedNodes.slice(1).map((currentNode, index) => {
    const previousNode = sortedNodes[index];
    const isActiveTarget = currentNode.progressStatus === "IN_PROGRESS";

    return {
      id: `${previousNode.nodeId}-${currentNode.nodeId}`,
      source: previousNode.nodeId,
      target: currentNode.nodeId,
      type: "smoothstep",
      animated: isActiveTarget,
      style: {
        stroke: isActiveTarget ? "#e6b94f" : "rgba(170, 163, 151, 0.34)",
        strokeWidth: isActiveTarget ? 2.25 : 1.7,
      },
      markerEnd: {
        type: MarkerType.ArrowClosed,
        color: isActiveTarget ? "#e6b94f" : "rgba(170, 163, 151, 0.5)",
      },
    };
  });

  return { nodes, edges };
};
