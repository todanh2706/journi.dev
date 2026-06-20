import dagre from "@dagrejs/dagre";
import type { RoadmapGraph, RoadmapSkillGraphNode } from "../types";

const NODE_WIDTH = 280;
const NODE_HEIGHT = 138;

export const layoutRoadmapGraph = ({ nodes, edges }: RoadmapGraph): RoadmapGraph => {
  const graph = new dagre.graphlib.Graph();

  graph.setDefaultEdgeLabel(() => ({}));
  graph.setGraph({
    rankdir: "TB",
    ranksep: 125,
    nodesep: 80,
    marginx: 36,
    marginy: 36,
  });

  nodes.forEach((node) => {
    graph.setNode(node.id, { width: NODE_WIDTH, height: NODE_HEIGHT });
  });

  edges.forEach((edge) => {
    graph.setEdge(edge.source, edge.target);
  });

  dagre.layout(graph);

  const positionedNodes: RoadmapSkillGraphNode[] = nodes.map((node) => {
    const layoutNode = graph.node(node.id);

    return {
      ...node,
      position: {
        x: layoutNode.x - NODE_WIDTH / 2,
        y: layoutNode.y - NODE_HEIGHT / 2,
      },
    };
  });

  return {
    nodes: positionedNodes,
    edges,
  };
};
