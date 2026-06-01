package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import journi.dev.backend.dtos.requests.NodePrerequisiteRequest;
import journi.dev.backend.dtos.responses.NodePrerequisiteResponse;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodePrerequisiteId;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.mappers.NodePrerequisiteMapper;

@Service
public class NodePrerequisiteService {
    private final NodePrerequisiteRepository nodePrerequisiteRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final NodePrerequisiteMapper prerequisiteMapper;

    public NodePrerequisiteService(NodePrerequisiteRepository nodePrerequisiteRepository,
            SkillNodeRepository skillNodeRepository, NodePrerequisiteMapper prerequisiteMapper) {
        this.nodePrerequisiteRepository = nodePrerequisiteRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.prerequisiteMapper = prerequisiteMapper;
    }

    @Transactional
    public NodePrerequisiteResponse createNodePrerequisite(UUID parentNodeId, NodePrerequisiteRequest request) {
        NodePrerequisite createdPrerequisite = prerequisiteMapper.toEntity(request);
        createdPrerequisite.setParentNodeId(parentNodeId);

        NodePrerequisite savedPrerequisite = nodePrerequisiteRepository.save(createdPrerequisite);

        return prerequisiteMapper.toResponse(savedPrerequisite);
    }

    public NodePrerequisiteResponse getNodePrerequisiteById(NodePrerequisiteId nodePrerequisiteId) {
        NodePrerequisite foundNodePrerequisite = nodePrerequisiteRepository.findById(nodePrerequisiteId)
                .orElseThrow(() -> new ResourceNotFoundException("Node prerequisite not found with id: " + nodePrerequisiteId));

        return prerequisiteMapper.toResponse(foundNodePrerequisite);
    }

    public List<NodePrerequisiteResponse> getAllNodePrerequisite() {
        return nodePrerequisiteRepository.findAll().stream()
                .map(prerequisiteMapper::toResponse)
                .collect(Collectors.toList());
    }
}
