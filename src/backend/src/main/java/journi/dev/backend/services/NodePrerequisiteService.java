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
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.exceptions.BadRequestException;
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
        if (request.getChildNodeId() == null) {
            throw new BadRequestException("Child node id is required");
        }

        if (parentNodeId.equals(request.getChildNodeId())) {
            throw new BadRequestException("A skill node cannot depend on itself");
        }

        SkillNode parentNode = skillNodeRepository.findById(parentNodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill node not found with id: " + parentNodeId));
        SkillNode childNode = skillNodeRepository.findById(request.getChildNodeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Skill node not found with id: " + request.getChildNodeId()));
        NodePrerequisiteId prerequisiteId = new NodePrerequisiteId(parentNodeId, request.getChildNodeId());

        if (nodePrerequisiteRepository.existsById(prerequisiteId)) {
            throw new BadRequestException("This prerequisite relationship already exists");
        }

        NodePrerequisite createdPrerequisite = prerequisiteMapper.toEntity(request);
        createdPrerequisite.setParentNode(parentNode);
        createdPrerequisite.setChildNode(childNode);

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
