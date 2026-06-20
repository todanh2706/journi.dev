import { useCallback, useMemo, useState } from "react";
import { Background, Controls, MiniMap, Panel, ReactFlow, ReactFlowProvider, useReactFlow, type NodeTypes } from "@xyflow/react";

import { buildRoadmapGraph } from "../utils/buildRoadmapGraph";
import { layoutRoadmapGraph } from "../utils/layoutRoadmapGraph";
import type { RoadmapCanvasProps, RoadmapSkillGraphEdge, RoadmapSkillGraphNode, RoadmapSkillNodeData } from "../types";
import { RoadmapNodeDrawer } from "./RoadmapNodeDrawer";
import { RoadmapSkillNode } from "./RoadmapSkillNode";
import { RoadmapToolbar } from "./RoadmapToolbar";

const nodeTypes: NodeTypes = { roadmapSkillNode: RoadmapSkillNode };

function RoadmapCanvasInner({ roadmap }: RoadmapCanvasProps) {
  const reactFlow = useReactFlow<RoadmapSkillGraphNode, RoadmapSkillGraphEdge>();
  const [selectedNode, setSelectedNode] = useState<RoadmapSkillNodeData | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const graph = useMemo(() => layoutRoadmapGraph(buildRoadmapGraph(roadmap.nodes)), [roadmap.nodes]);
  const normalizedSearchTerm = searchTerm.trim().toLowerCase();

  const nodes = useMemo<RoadmapSkillGraphNode[]>(() => {
    if (!normalizedSearchTerm) return graph.nodes;
    return graph.nodes.map((node) => {
      const isSearchMatch = node.data.title.toLowerCase().includes(normalizedSearchTerm);
      return { ...node, data: { ...node.data, isSearchMatch, isSearchDimmed: !isSearchMatch } };
    });
  }, [graph.nodes, normalizedSearchTerm]);

  const completedCount = useMemo(() => roadmap.nodes.filter((node) => node.progressStatus === "COMPLETED").length, [roadmap.nodes]);
  const matchCount = useMemo(() => normalizedSearchTerm ? nodes.filter((node) => node.data.isSearchMatch).length : 0, [nodes, normalizedSearchTerm]);

  const closeDrawer = useCallback(() => {
    const nodeId = selectedNode?.skillNode.nodeId;
    setSelectedNode(null);
    if (nodeId) requestAnimationFrame(() => document.querySelector<HTMLElement>(`.react-flow__node[data-id="${CSS.escape(nodeId)}"]`)?.focus());
  }, [selectedNode]);

  return (
    <div className="relative h-[calc(100dvh-240px)] min-h-[560px] overflow-hidden rounded-2xl border border-line bg-shell shadow-2xl shadow-black/25 sm:h-[calc(100dvh-250px)]">
      <ReactFlow<RoadmapSkillGraphNode, RoadmapSkillGraphEdge>
        nodes={nodes}
        edges={graph.edges}
        nodeTypes={nodeTypes}
        fitView
        fitViewOptions={{ padding: 0.24 }}
        minZoom={0.25}
        maxZoom={1.6}
        onlyRenderVisibleElements
        nodesDraggable={false}
        nodesConnectable={false}
        nodesFocusable
        edgesFocusable={false}
        edgesReconnectable={false}
        selectNodesOnDrag={false}
        autoPanOnNodeFocus
        elevateNodesOnSelect={false}
        elevateEdgesOnSelect={false}
        elementsSelectable
        onNodeClick={(_, node) => setSelectedNode(node.data)}
        proOptions={{ hideAttribution: true }}
        className="roadmap-canvas"
      >
        <Background color="rgba(170, 163, 151, 0.16)" gap={30} size={1} />
        <Controls showInteractive={false} />
        <MiniMap
          pannable
          zoomable={false}
          bgColor="#0d0c09"
          maskColor="rgba(8, 8, 6, 0.7)"
          maskStrokeColor="#e6b94f"
          maskStrokeWidth={1.5}
          nodeBorderRadius={8}
          nodeStrokeWidth={7}
          offsetScale={12}
          ariaLabel="Roadmap overview"
          nodeColor={(node) => {
            const data = node.data as RoadmapSkillNodeData;
            if (data.isLocked) return "#4a4439";
            if (data.progressStatus === "COMPLETED") return "#63c38a";
            return "#e6b94f";
          }}
          nodeStrokeColor="#f5f1e8"
        />
        <Panel position="top-left" className="!m-3 sm:!m-4">
          <RoadmapToolbar completedCount={completedCount} totalCount={roadmap.nodes.length} matchCount={matchCount} searchTerm={searchTerm} onSearchTermChange={setSearchTerm} onFitView={() => void reactFlow.fitView({ padding: 0.22, duration: 420 })} />
        </Panel>
      </ReactFlow>

      <RoadmapNodeDrawer node={selectedNode} onClose={closeDrawer} />
    </div>
  );
}

export function RoadmapCanvas(props: RoadmapCanvasProps) {
  return <ReactFlowProvider><RoadmapCanvasInner {...props} /></ReactFlowProvider>;
}
