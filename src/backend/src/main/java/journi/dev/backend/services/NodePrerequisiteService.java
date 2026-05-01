package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import journi.dev.backend.dtos.requests.NodePrerequisiteRequest;
import journi.dev.backend.dtos.responses.NodePrerequisiteResponse;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodePrerequisiteId;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;

@Service
public class NodePrerequisiteService {
    private final NodePrerequisiteRepository nodePrerequisiteRepository;
    private final SkillNodeRepository skillNodeRepository;

    public NodePrerequisiteService(NodePrerequisiteRepository nodePrerequisiteRepository,
            SkillNodeRepository skillNodeRepository) {
        this.nodePrerequisiteRepository = nodePrerequisiteRepository;
        this.skillNodeRepository = skillNodeRepository;
    }

    public NodePrerequisiteResponse createNodePrerequisite(UUID parentNodeId, NodePrerequisiteRequest request) {
        NodePrerequisite createdPrerequisite = new NodePrerequisite();
        createdPrerequisite.setParentNodeId(parentNodeId);
        createdPrerequisite.setChildNodeId(request.getChildNodeId());
        createdPrerequisite.setRelationType(request.getRelationType());

        NodePrerequisite savedPrerequisite = nodePrerequisiteRepository.save(createdPrerequisite);

        return new NodePrerequisiteResponse(
                savedPrerequisite.getParentNodeId(),
                savedPrerequisite.getChildNodeId(),
                savedPrerequisite.getRelationType(),
                savedPrerequisite.getCreatedAt());
    }

    public NodePrerequisiteResponse getNodePrerequisiteById(NodePrerequisiteId nodePrerequisiteId) {
        NodePrerequisite foundNodePrerequisite = nodePrerequisiteRepository.findById(nodePrerequisiteId).orElse(null);

        if (foundNodePrerequisite == null)
            return null;

        return new NodePrerequisiteResponse(
                foundNodePrerequisite.getParentNodeId(),
                foundNodePrerequisite.getChildNodeId(),
                foundNodePrerequisite.getRelationType(),
                foundNodePrerequisite.getCreatedAt());
    }

    public List<NodePrerequisiteResponse> getAllNodePrerequisite() {
        return nodePrerequisiteRepository.findAll().stream().map(prerequisite -> new NodePrerequisiteResponse(
                prerequisite.getParentNodeId(),
                prerequisite.getChildNodeId(),
                prerequisite.getRelationType(),
                prerequisite.getCreatedAt())).collect(Collectors.toList());
    }
}
