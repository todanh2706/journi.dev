import { useMemo, useState } from "react";
import {
  Background,
  Controls,
  MiniMap,
  Panel,
  ReactFlow,
  ReactFlowProvider,
  useReactFlow,
  type NodeTypes,
} from "@xyflow/react";
import { buildRoadmapGraph } from "../utils/buildRoadmapGraph";
import { layoutRoadmapGraph } from "../utils/layoutRoadmapGraph";
import type { RoadmapCanvasProps, RoadmapSkillGraphEdge, RoadmapSkillGraphNode, RoadmapSkillNodeData } from "../types";
import { RoadmapNodeDrawer } from "./RoadmapNodeDrawer";
import { RoadmapSkillNode } from "./RoadmapSkillNode";
import { RoadmapToolbar } from "./RoadmapToolbar";

const nodeTypes: NodeTypes = {
  roadmapSkillNode: RoadmapSkillNode,
};

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

      return {
        ...node,
        data: {
          ...node.data,
          isSearchMatch,
          isSearchDimmed: !isSearchMatch,
        },
      };
    });
  }, [graph.nodes, normalizedSearchTerm]);

  const completedCount = useMemo(
    () => roadmap.nodes.filter((node) => node.progressStatus === "COMPLETED").length,
    [roadmap.nodes],
  );

  const handleFitView = () => {
    void reactFlow.fitView({ padding: 0.22, duration: 520 });
  };

  return (
    <div className="relative h-[calc(100vh-235px)] min-h-[620px] overflow-hidden rounded-2xl border border-white/[0.07] bg-[#070814] shadow-2xl shadow-black/30">
      <div className="pointer-events-none absolute inset-0 bg-[radial-gradient(circle_at_20%_0%,rgba(56,189,248,0.13),transparent_28%),radial-gradient(circle_at_80%_20%,rgba(168,85,247,0.14),transparent_32%),linear-gradient(180deg,rgba(15,23,42,0.7),rgba(2,6,23,0.95))]" />
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
        nodesFocusable={false}
        edgesFocusable={false}
        edgesReconnectable={false}
        selectNodesOnDrag={false}
        autoPanOnNodeFocus={false}
        elevateNodesOnSelect={false}
        elevateEdgesOnSelect={false}
        elementsSelectable
        onNodeClick={(_, node) => setSelectedNode(node.data)}
        proOptions={{ hideAttribution: true }}
        className="roadmap-canvas relative z-10"
      >
        <Background color="rgba(148, 163, 184, 0.18)" gap={28} size={1.2} />
        <Controls
          className="!border !border-white/[0.08] !bg-slate-950/85 !shadow-2xl !shadow-black/30 [&_button]:!border-white/[0.08] [&_button]:!bg-transparent [&_button]:!text-slate-200 [&_button:hover]:!bg-violet-400/10"
          showInteractive={false}
        />
        <MiniMap
          pannable
          zoomable={false}
          bgColor="rgba(2, 6, 23, 0.92)"
          maskColor="rgba(8, 13, 31, 0.62)"
          maskStrokeColor="#67e8f9"
          maskStrokeWidth={1.5}
          nodeBorderRadius={10}
          nodeStrokeWidth={8}
          offsetScale={12}
          style={{ width: 220, height: 150 }}
          nodeColor={(node) => {
            const data = node.data as RoadmapSkillNodeData;
            if (data.isLocked) return "#475569";
            if (data.progressStatus === "COMPLETED") return "#10b981";
            if (data.progressStatus === "IN_PROGRESS") return "#8b5cf6";
            return "#38bdf8";
          }}
          nodeStrokeColor={(node) => {
            const data = node.data as RoadmapSkillNodeData;
            if (data.progressStatus === "COMPLETED") return "#a7f3d0";
            if (data.progressStatus === "IN_PROGRESS") return "#ddd6fe";
            return "rgba(226, 232, 240, 0.75)";
          }}
          className="roadmap-minimap !shadow-2xl !shadow-black/30"
        />
        <Panel position="top-left" className="!m-4">
          <RoadmapToolbar
            completedCount={completedCount}
            totalCount={roadmap.nodes.length}
            searchTerm={searchTerm}
            onSearchTermChange={setSearchTerm}
            onFitView={handleFitView}
          />
        </Panel>
      </ReactFlow>

      <RoadmapNodeDrawer node={selectedNode} onClose={() => setSelectedNode(null)} />
    </div>
  );
}

export function RoadmapCanvas(props: RoadmapCanvasProps) {
  return (
    <ReactFlowProvider>
      <RoadmapCanvasInner {...props} />
    </ReactFlowProvider>
  );
}
