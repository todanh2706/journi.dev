export async function completeRoadmapNode(
  nodeId: string,
  completeNode: (nodeId: string) => Promise<unknown>,
  refreshRoadmap: () => Promise<unknown>,
): Promise<void> {
  await completeNode(nodeId);
  await refreshRoadmap();
}
