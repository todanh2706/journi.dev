package journi.dev.backend.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.dtos.requests.SkillNodeRequest;
import journi.dev.backend.configurations.PracticeSubmissionProperties;
import journi.dev.backend.dtos.responses.LearningResourceResponse;
import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.entities.LearningContent;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.mappers.SkillNodeMapper;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.LearningContentRepository;
import journi.dev.backend.repositories.ChallengeRepository;
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
    private final LearningContentRepository learningContentRepository;
    private final ChallengeRepository challengeRepository;
    private final PracticeSubmissionProperties practiceSubmissionProperties;
    private final ObjectMapper objectMapper;

    public SkillNodeService(SkillNodeRepository skillNodeRepository, UserRepository userRepository,
            LearningRoadmapRepository roadmapRepository, SkillNodeMapper skillNodeMapper,
            UserNodeProgressService userNodeProgressService, LearningContentRepository learningContentRepository,
            ChallengeRepository challengeRepository, ObjectMapper objectMapper,
            PracticeSubmissionProperties practiceSubmissionProperties) {
        this.skillNodeRepository = skillNodeRepository;
        this.userRepository = userRepository;
        this.roadmapRepository = roadmapRepository;
        this.skillNodeMapper = skillNodeMapper;
        this.userNodeProgressService = userNodeProgressService;
        this.learningContentRepository = learningContentRepository;
        this.challengeRepository = challengeRepository;
        this.practiceSubmissionProperties = practiceSubmissionProperties;
        this.objectMapper = objectMapper;
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
        List<UUID> unlockedNodeIds = nodes.stream()
                .filter(node -> progressStatusByNodeId.getOrDefault(node.getNodeId(), ProgressStatus.LOCKED)
                        != ProgressStatus.LOCKED)
                .map(SkillNode::getNodeId)
                .toList();
        Map<UUID, List<LearningContent>> resourcesByNodeId = getResourcesByNodeId(unlockedNodeIds);
        Map<UUID, journi.dev.backend.entities.Challenge> requiredChallengeByNodeId = challengeRepository
                .findByNode_NodeIdIn(nodes.stream().map(SkillNode::getNodeId).toList())
                .stream()
                .filter(challenge -> Boolean.TRUE.equals(challenge.getIsRequired()))
                .collect(Collectors.toMap(
                        challenge -> challenge.getNode().getNodeId(),
                        challenge -> challenge,
                        (first, ignored) -> first));

        return nodes.stream().map(node -> {
            SkillNodeResponse response = skillNodeMapper.toResponse(node);
            ProgressStatus progressStatus = progressStatusByNodeId.getOrDefault(node.getNodeId(), ProgressStatus.LOCKED);
            response.setProgressStatus(progressStatus);
            response.setIsLocked(progressStatus == ProgressStatus.LOCKED);
            journi.dev.backend.entities.Challenge requiredChallenge = requiredChallengeByNodeId.get(node.getNodeId());
            response.setHasRequiredChallenge(requiredChallenge != null);
            response.setStarterRepositoryUrl(requiredChallenge != null && progressStatus != ProgressStatus.LOCKED
                    ? requiredChallenge.getStarterRepositoryUrl()
                    : null);
            response.setPracticeSubmissionEnabled(requiredChallenge != null
                    && progressStatus != ProgressStatus.LOCKED
                    && requiredChallenge.isEvaluationEnabled()
                    && practiceSubmissionProperties.isEnabled());

            if (progressStatus == ProgressStatus.LOCKED) {
                redactLearningDetails(response);
            } else {
                applyLearningDetails(response, parseLearningDetails(node.getContentJson()),
                        resourcesByNodeId.getOrDefault(node.getNodeId(), List.of()));
            }

            return response;
        }).toList();
    }

    private Map<UUID, List<LearningContent>> getResourcesByNodeId(Collection<UUID> nodeIds) {
        if (nodeIds.isEmpty()) {
            return Map.of();
        }

        return learningContentRepository.findByNode_NodeIdIn(nodeIds).stream()
                .collect(Collectors.groupingBy(resource -> resource.getNode().getNodeId()));
    }

    private void applyLearningDetails(SkillNodeResponse response, NodeLearningDetails details,
            List<LearningContent> resources) {
        response.setSummary(details.summary());
        response.setLevel(details.level());
        response.setEstimatedHours(details.estimatedHours());
        response.setNote(details.note());
        response.setChecklist(details.checklist());
        response.setLearningResources(resources.stream()
                .map(resource -> new LearningResourceResponse(
                        resource.getTitle(),
                        resource.getSourceType(),
                        resource.getSourceUrl(),
                        resource.getContentBody()))
                .toList());
    }

    private void redactLearningDetails(SkillNodeResponse response) {
        response.setContentJson(null);
        response.setSummary(null);
        response.setLevel(null);
        response.setEstimatedHours(null);
        response.setNote(null);
        response.setChecklist(List.of());
        response.setLearningResources(List.of());
    }

    private NodeLearningDetails parseLearningDetails(String contentJson) {
        if (contentJson == null || contentJson.isBlank()) {
            return NodeLearningDetails.empty();
        }

        try {
            JsonNode content = objectMapper.readTree(contentJson);
            if (content == null || !content.isObject()) {
                return NodeLearningDetails.empty();
            }

            return new NodeLearningDetails(
                    textValue(content, "summary"),
                    textValue(content, "level"),
                    integerValue(content, "estimatedHours"),
                    textValue(content, "note"),
                    stringList(content.get("checklist")));
        } catch (JsonProcessingException exception) {
            return NodeLearningDetails.empty();
        }
    }

    private String textValue(JsonNode content, String fieldName) {
        JsonNode value = content.get(fieldName);
        return value != null && value.isTextual() && !value.asText().isBlank() ? value.asText() : null;
    }

    private Integer integerValue(JsonNode content, String fieldName) {
        JsonNode value = content.get(fieldName);
        return value != null && value.isIntegralNumber() && value.canConvertToInt() ? value.intValue() : null;
    }

    private List<String> stringList(JsonNode value) {
        if (value == null || !value.isArray()) {
            return List.of();
        }

        return java.util.stream.StreamSupport.stream(value.spliterator(), false)
                .filter(JsonNode::isTextual)
                .map(JsonNode::asText)
                .filter(item -> !item.isBlank())
                .toList();
    }

    private record NodeLearningDetails(
            String summary,
            String level,
            Integer estimatedHours,
            String note,
            List<String> checklist) {

        private static NodeLearningDetails empty() {
            return new NodeLearningDetails(null, null, null, null, List.of());
        }
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
