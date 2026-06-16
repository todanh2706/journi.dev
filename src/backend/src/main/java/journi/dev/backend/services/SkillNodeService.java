package journi.dev.backend.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.mappers.SkillNodeMapper;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.utils.SlugUtils;

@Service
public class SkillNodeService {
    private final SkillNodeRepository skillNodeRepository;
    private final UserRepository userRepository;
    private final LearningRoadmapRepository roadmapRepository;
    private final SkillNodeMapper skillNodeMapper;
    private final UserNodeProgressService userNodeProgressService;

    public SkillNodeService(SkillNodeRepository skillNodeRepository, UserRepository userRepository,
            LearningRoadmapRepository roadmapRepository, SkillNodeMapper skillNodeMapper,
            UserNodeProgressService userNodeProgressService) {
        this.skillNodeRepository = skillNodeRepository;
        this.userRepository = userRepository;
        this.roadmapRepository = roadmapRepository;
        this.skillNodeMapper = skillNodeMapper;
        this.userNodeProgressService = userNodeProgressService;
    }

    @Transactional
    public SkillNodeResponse createSkillNode(UUID creatorId, SkillNodeRequest request) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorId));
        UUID roadmapId = request.getRoadmapId();
        if (roadmapId == null) {
            throw new BadRequestException("Roadmap id is required");
        }

        LearningRoadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap not found with id: " + roadmapId));

        SkillNode newNode = skillNodeMapper.toEntity(request);
        newNode.setRoadmap(roadmap);
        newNode.setSlug(resolveNodeSlug(roadmapId, request));
        newNode.setNodeType(NodeType.from(request.getNodeType()));
        newNode.setCreatedBy(creator.getUserId());
        SkillNode savedNode = skillNodeRepository.save(newNode);

        return mapNodesWithProgress(creator, List.of(savedNode)).get(0);
    }

    @Transactional(readOnly = true)
    public List<SkillNodeResponse> getAllNodes(User currentUser) {
        List<SkillNode> nodes = skillNodeRepository.findAll(Sort.by(Sort.Direction.ASC, "orderIndex"));
        return mapNodesWithProgress(currentUser, nodes);
    }

    @Transactional(readOnly = true)
    public SkillNodeResponse getNodeById(UUID nodeId, User currentUser) {
        SkillNode foundNode = skillNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill node not found with id: " + nodeId));

        return mapNodesWithProgress(currentUser, List.of(foundNode)).get(0);
    }

    @Transactional(readOnly = true)
    public List<SkillNodeResponse> getNodesByRoadmap(UUID roadmapId, User currentUser) {
        List<SkillNode> roadmapNodes = skillNodeRepository.findByRoadmap_RoadmapIdOrderByOrderIndexAsc(roadmapId);
        return mapNodesWithProgress(currentUser, roadmapNodes);
    }

    private List<SkillNodeResponse> mapNodesWithProgress(User currentUser, List<SkillNode> nodes) {
        Map<UUID, ProgressStatus> progressStatusByNodeId = userNodeProgressService.getComputedStatuses(currentUser, nodes);

        return nodes.stream().map(node -> {
            SkillNodeResponse response = skillNodeMapper.toResponse(node);
            ProgressStatus progressStatus = progressStatusByNodeId.getOrDefault(node.getNodeId(), ProgressStatus.LOCKED);
            response.setProgressStatus(progressStatus);
            response.setIsLocked(progressStatus == ProgressStatus.LOCKED);
            return response;
        }).toList();
    }

    private String resolveNodeSlug(UUID roadmapId, SkillNodeRequest request) {
        String explicitSlug = SlugUtils.toSlug(request.getSlug());
        if (!explicitSlug.isBlank()) {
            if (skillNodeRepository.existsByRoadmap_RoadmapIdAndSlug(roadmapId, explicitSlug)) {
                throw new BadRequestException("Skill node slug already exists in this roadmap: " + explicitSlug);
            }

            return explicitSlug;
        }

        String baseSlug = SlugUtils.toSlug(request.getTitle());
        if (baseSlug.isBlank()) {
            throw new BadRequestException("Skill node slug could not be generated");
        }

        String candidateSlug = baseSlug;
        int suffix = 2;
        while (skillNodeRepository.existsByRoadmap_RoadmapIdAndSlug(roadmapId, candidateSlug)) {
            candidateSlug = baseSlug + "-" + suffix++;
        }

        return candidateSlug;
    }
}
