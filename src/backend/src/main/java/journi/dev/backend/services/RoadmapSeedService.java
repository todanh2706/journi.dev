package journi.dev.backend.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.configurations.RoadmapSeedProperties;
import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.LearningContent;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.LearningContentRepository;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.services.seed.RoadmapSeedData;
import journi.dev.backend.services.seed.RoadmapSeedData.ChallengeDefinition;
import journi.dev.backend.services.seed.RoadmapSeedData.NodeDefinition;
import journi.dev.backend.services.seed.RoadmapSeedData.ResourceDefinition;

@Service
public class RoadmapSeedService {
    private static final String SEED_OWNER_USERNAME = "system_roadmap_seed";
    private static final String SEED_OWNER_EMAIL = "system+roadmaps@journi.dev";
    private static final String SEED_OWNER_PASSWORD = "system-roadmap-seed";
    private static final String REQUIRED_RELATION_TYPE = "REQUIRED";
    private static final String PINNED_IMAGE_PATTERN = "^[^\\s]+@sha256:[0-9a-f]{64}$";
    private static final String MAIN_SOURCE_REPOSITORY_URL = "https://github.com/todanh2706/journi.dev";
    private static final String GITHUB_OWNER_PATTERN = "^[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?$";
    private static final String GITHUB_REPOSITORY_PATTERN = "^[A-Za-z0-9._-]+$";

    private final RoadmapSeedProperties roadmapSeedProperties;
    private final RoadmapSeedDataLoader roadmapSeedDataLoader;
    private final LearningRoadmapRepository learningRoadmapRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final NodePrerequisiteRepository nodePrerequisiteRepository;
    private final LearningContentRepository learningContentRepository;
    private final ChallengeRepository challengeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public RoadmapSeedService(
            RoadmapSeedProperties roadmapSeedProperties,
            RoadmapSeedDataLoader roadmapSeedDataLoader,
            LearningRoadmapRepository learningRoadmapRepository,
            SkillNodeRepository skillNodeRepository,
            NodePrerequisiteRepository nodePrerequisiteRepository,
            LearningContentRepository learningContentRepository,
            ChallengeRepository challengeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ObjectMapper objectMapper) {
        this.roadmapSeedProperties = roadmapSeedProperties;
        this.roadmapSeedDataLoader = roadmapSeedDataLoader;
        this.learningRoadmapRepository = learningRoadmapRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.nodePrerequisiteRepository = nodePrerequisiteRepository;
        this.learningContentRepository = learningContentRepository;
        this.challengeRepository = challengeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public RoadmapSeedResult seedConfiguredRoadmaps() {
        return seedFromLocation(roadmapSeedProperties.getDatasetLocation());
    }

    @Transactional
    public RoadmapSeedResult seedFromLocation(String datasetLocation) {
        RoadmapSeedData dataset = roadmapSeedDataLoader.load(requireText(datasetLocation, "Dataset location"));
        validateDataset(dataset);

        User seedOwner = upsertSeedOwner();
        LearningRoadmap roadmap = upsertRoadmap(seedOwner, dataset);
        Map<String, SkillNode> nodesBySlug = upsertNodes(seedOwner, roadmap, dataset.nodes());

        clearScopedRelationships(nodesBySlug.values());

        int resourceCount = syncLearningContent(nodesBySlug, dataset.nodes());
        int challengeCount = syncChallenges(seedOwner, nodesBySlug, dataset.nodes());
        int prerequisiteCount = syncPrerequisites(nodesBySlug, dataset.nodes());

        return new RoadmapSeedResult(
                roadmap.getSlug(),
                nodesBySlug.size(),
                resourceCount,
                challengeCount,
                prerequisiteCount);
    }

    private void validateDataset(RoadmapSeedData dataset) {
        if (dataset == null) {
            throw new IllegalStateException("Roadmap seed dataset is required");
        }
        if (dataset.roadmap() == null) {
            throw new IllegalStateException("Roadmap seed dataset must include roadmap metadata");
        }
        if (dataset.nodes() == null || dataset.nodes().isEmpty()) {
            throw new IllegalStateException("Roadmap seed dataset must include at least one node");
        }

        Set<String> nodeSlugs = new LinkedHashSet<>();
        Set<Integer> orderIndexes = new LinkedHashSet<>();
        Set<String> starterRepositoryUrls = new LinkedHashSet<>();
        for (NodeDefinition node : dataset.nodes()) {
            String slug = requireText(node.slug(), "Node slug");
            if (!nodeSlugs.add(slug)) {
                throw new IllegalStateException("Duplicate node slug in roadmap seed dataset: " + slug);
            }

            Integer orderIndex = Objects.requireNonNull(node.orderIndex(), "Node order index is required");
            if (!orderIndexes.add(orderIndex)) {
                throw new IllegalStateException("Duplicate node order index in roadmap seed dataset: " + orderIndex);
            }

            NodeType nodeType = NodeType.from(node.nodeType());
            boolean assessmentNode = nodeType == NodeType.PRACTICE || nodeType == NodeType.PROJECT;
            if (assessmentNode && node.challenge() == null) {
                throw new IllegalStateException("Assessment node requires a challenge: " + slug);
            }
            if (!assessmentNode && node.challenge() != null) {
                throw new IllegalStateException("Only PRACTICE and PROJECT nodes may define a challenge: " + slug);
            }
            if (node.challenge() != null) {
                String starterRepositoryUrl = validateChallenge(node.challenge(), slug);
                if (!starterRepositoryUrls.add(starterRepositoryUrl.toLowerCase(Locale.ROOT))) {
                    throw new IllegalStateException(
                            "Duplicate challenge starter repository in roadmap seed dataset: "
                                    + starterRepositoryUrl);
                }
            }
        }
    }

    private String validateChallenge(ChallengeDefinition challenge, String nodeSlug) {
        requireText(challenge.title(), "Challenge title");
        requireText(challenge.description(), "Challenge description");
        requireText(challenge.difficulty(), "Challenge difficulty");
        requireText(challenge.instructions(), "Challenge instructions");
        requireNonEmptyTextList(challenge.acceptanceCriteria(), "Challenge acceptance criteria", nodeSlug);
        requireNonEmptyTextList(challenge.hints(), "Challenge hints", nodeSlug);
        requireNonEmptyTextList(challenge.expectedArtifacts(), "Challenge expected artifacts", nodeSlug);
        requireNonEmptyTextList(challenge.graderCommand(), "Challenge grader command", nodeSlug);

        Integer maxScore = Objects.requireNonNull(challenge.maxScore(), "Challenge max score is required");
        Integer passingScore = Objects.requireNonNull(challenge.passingScore(), "Challenge passing score is required");
        if (maxScore <= 0 || passingScore <= 0 || passingScore > maxScore) {
            throw new IllegalStateException("Challenge passing score must be between 1 and max score: " + nodeSlug);
        }

        Integer timeoutSeconds = Objects.requireNonNull(challenge.timeoutSeconds(),
                "Challenge timeout is required");
        if (timeoutSeconds < 10 || timeoutSeconds > 900) {
            throw new IllegalStateException("Challenge timeout must be between 10 and 900 seconds: " + nodeSlug);
        }

        String starterRepositoryUrl = requireText(challenge.starterRepositoryUrl(),
                "Challenge starter repository url");
        String validatedStarterRepositoryUrl = validateStarterRepositoryUrl(starterRepositoryUrl, nodeSlug);

        String graderImage = requireText(challenge.graderImage(), "Challenge grader image");
        if (!graderImage.matches(PINNED_IMAGE_PATTERN)) {
            throw new IllegalStateException("Challenge grader image must be pinned by sha256 digest: " + nodeSlug);
        }
        Objects.requireNonNull(challenge.evaluationEnabled(),
                "Challenge evaluation enabled flag is required: " + nodeSlug);
        if (!Boolean.TRUE.equals(challenge.isRequired())) {
            throw new IllegalStateException("Seeded assessment challenge must be required: " + nodeSlug);
        }
        return validatedStarterRepositoryUrl;
    }

    private String validateStarterRepositoryUrl(String starterRepositoryUrl, String nodeSlug) {
        URI starterUri;
        try {
            starterUri = URI.create(starterRepositoryUrl);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("Challenge starter repository url is invalid: " + nodeSlug, exception);
        }

        if (!"https".equalsIgnoreCase(starterUri.getScheme())
                || !"github.com".equalsIgnoreCase(starterUri.getHost())
                || starterUri.getUserInfo() != null
                || starterUri.getPort() != -1
                || starterUri.getQuery() != null
                || starterUri.getFragment() != null) {
            throw new IllegalStateException("Challenge starter repository must use public GitHub HTTPS: " + nodeSlug);
        }

        List<String> pathSegments = Arrays.stream((starterUri.getPath() == null ? "" : starterUri.getPath()).split("/"))
                .filter(segment -> !segment.isBlank())
                .toList();
        if (pathSegments.size() != 2) {
            throw new IllegalStateException(
                    "Challenge starter repository must be a GitHub repository URL: " + nodeSlug);
        }

        String owner = pathSegments.get(0);
        String repository = pathSegments.get(1);
        if (!owner.matches(GITHUB_OWNER_PATTERN)
                || !repository.matches(GITHUB_REPOSITORY_PATTERN)
                || repository.toLowerCase(Locale.ROOT).endsWith(".git")) {
            throw new IllegalStateException(
                    "Challenge starter repository must be a valid GitHub owner/repository URL: " + nodeSlug);
        }

        String canonicalUrl = "https://github.com/" + owner + "/" + repository;
        if (MAIN_SOURCE_REPOSITORY_URL.equalsIgnoreCase(canonicalUrl)) {
            throw new IllegalStateException(
                    "Challenge starter repository must not point to the main Journi.dev source repository: "
                            + nodeSlug);
        }
        return canonicalUrl;
    }

    private User upsertSeedOwner() {
        User seedOwner = userRepository.findByUsername(SEED_OWNER_USERNAME)
                .or(() -> userRepository.findByEmail(SEED_OWNER_EMAIL))
                .orElseGet(User::new);

        seedOwner.setUsername(SEED_OWNER_USERNAME);
        seedOwner.setEmail(SEED_OWNER_EMAIL);
        seedOwner.setPasswordHash(passwordEncoder.encode(SEED_OWNER_PASSWORD));
        seedOwner.setRole(UserRole.USER);
        seedOwner.setStatus(UserStatus.ACTIVE);
        seedOwner.setEnabled(true);
        seedOwner.setVerificationCode(null);
        seedOwner.setVerificationExpiration(null);

        return userRepository.saveAndFlush(seedOwner);
    }

    private LearningRoadmap upsertRoadmap(User seedOwner, RoadmapSeedData dataset) {
        RoadmapSeedData.RoadmapDefinition roadmapDefinition = dataset.roadmap();
        LearningRoadmap roadmap = learningRoadmapRepository.findBySlug(requireText(roadmapDefinition.slug(), "Roadmap slug"))
                .orElseGet(LearningRoadmap::new);

        roadmap.setOwner(seedOwner);
        roadmap.setTitle(requireText(roadmapDefinition.title(), "Roadmap title"));
        roadmap.setSlug(requireText(roadmapDefinition.slug(), "Roadmap slug"));
        roadmap.setDescription(requireText(roadmapDefinition.description(), "Roadmap description"));
        roadmap.setVisibility(requireText(roadmapDefinition.visibility(), "Roadmap visibility"));
        roadmap.setIsDynamic(Boolean.TRUE.equals(roadmapDefinition.isDynamic()));

        if (roadmap.getCreatedBy() == null) {
            roadmap.setCreatedBy(seedOwner.getUserId());
        }
        roadmap.setUpdatedBy(seedOwner.getUserId());

        return learningRoadmapRepository.saveAndFlush(roadmap);
    }

    private Map<String, SkillNode> upsertNodes(User seedOwner, LearningRoadmap roadmap, List<NodeDefinition> nodeDefinitions) {
        Map<String, SkillNode> existingNodesBySlug = skillNodeRepository
                .findByRoadmap_RoadmapIdOrderByOrderIndexAsc(roadmap.getRoadmapId())
                .stream()
                .collect(Collectors.toMap(
                        SkillNode::getSlug,
                        node -> node,
                        (left, right) -> left,
                        LinkedHashMap::new));

        Map<String, SkillNode> nodesBySlug = new LinkedHashMap<>();
        for (NodeDefinition nodeDefinition : nodeDefinitions) {
            String nodeSlug = requireText(nodeDefinition.slug(), "Node slug");
            SkillNode node = existingNodesBySlug.getOrDefault(nodeSlug, new SkillNode());
            boolean isNewNode = node.getNodeId() == null;

            node.setRoadmap(roadmap);
            node.setTitle(requireText(nodeDefinition.title(), "Node title"));
            node.setSlug(nodeSlug);
            node.setOrderIndex(Objects.requireNonNull(nodeDefinition.orderIndex(), "Node order index is required"));
            node.setNodeType(NodeType.from(nodeDefinition.nodeType()));
            node.setContentJson(buildContentJson(nodeDefinition));
            if (isNewNode || node.getCreatedBy() == null) {
                node.setCreatedBy(seedOwner.getUserId());
            }
            node.setUpdatedBy(seedOwner.getUserId());

            SkillNode savedNode = skillNodeRepository.save(node);
            nodesBySlug.put(nodeSlug, savedNode);
        }

        skillNodeRepository.flush();
        return nodesBySlug;
    }

    private String buildContentJson(NodeDefinition nodeDefinition) {
        Map<String, Object> content = new LinkedHashMap<>();
        content.put("summary", requireText(nodeDefinition.summary(), "Node summary"));
        content.put("level", requireText(nodeDefinition.level(), "Node level"));
        content.put("estimatedHours", Objects.requireNonNull(nodeDefinition.estimatedHours(),
                "Node estimated hours is required"));
        content.put("note", requireText(nodeDefinition.note(), "Node note"));
        content.put("checklist", defaultList(nodeDefinition.checklist()));

        try {
            return objectMapper.writeValueAsString(content);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize contentJson for node: " + nodeDefinition.slug(),
                    exception);
        }
    }

    private void clearScopedRelationships(Collection<SkillNode> nodes) {
        List<UUID> nodeIds = nodes.stream()
                .map(SkillNode::getNodeId)
                .filter(Objects::nonNull)
                .toList();

        if (nodeIds.isEmpty()) {
            return;
        }

        nodePrerequisiteRepository.deleteByNodeIds(nodeIds);
        learningContentRepository.deleteByNodeIds(nodeIds);
    }

    private int syncLearningContent(Map<String, SkillNode> nodesBySlug, List<NodeDefinition> nodeDefinitions) {
        int createdCount = 0;

        for (NodeDefinition nodeDefinition : nodeDefinitions) {
            SkillNode node = nodesBySlug.get(nodeDefinition.slug());
            for (ResourceDefinition resourceDefinition : defaultList(nodeDefinition.resources())) {
                LearningContent learningContent = new LearningContent();
                learningContent.setNode(node);
                learningContent.setSourceType(requireText(resourceDefinition.sourceType(), "Resource source type"));
                learningContent.setSourceUrl(requireText(resourceDefinition.sourceUrl(), "Resource source url"));
                learningContent.setTitle(requireText(resourceDefinition.title(), "Resource title"));
                learningContent.setContentBody(requireText(resourceDefinition.contentBody(), "Resource content body"));
                learningContent.setMetaJson(writeJson(resourceDefinition.meta(), "resource meta", nodeDefinition.slug()));
                learningContent.setSyncedAt(LocalDateTime.now());
                learningContentRepository.save(learningContent);
                createdCount++;
            }
        }

        learningContentRepository.flush();
        return createdCount;
    }

    private int syncChallenges(User seedOwner, Map<String, SkillNode> nodesBySlug, List<NodeDefinition> nodeDefinitions) {
        int syncedCount = 0;

        for (NodeDefinition nodeDefinition : nodeDefinitions) {
            ChallengeDefinition challengeDefinition = nodeDefinition.challenge();
            if (challengeDefinition == null) {
                continue;
            }

            SkillNode node = nodesBySlug.get(nodeDefinition.slug());
            Challenge challenge = challengeRepository.findByNode_NodeId(node.getNodeId()).stream()
                    .findFirst()
                    .orElseGet(Challenge::new);
            challenge.setNode(node);
            challenge.setTitle(requireText(challengeDefinition.title(), "Challenge title"));
            challenge.setDescription(requireText(challengeDefinition.description(), "Challenge description"));
            challenge.setDifficulty(requireText(challengeDefinition.difficulty(), "Challenge difficulty"));
            challenge.setMaxScore(Objects.requireNonNull(challengeDefinition.maxScore(), "Challenge max score is required"));
            challenge.setIsRequired(Boolean.TRUE.equals(challengeDefinition.isRequired()));
            challenge.setInstructions(requireText(challengeDefinition.instructions(), "Challenge instructions"));
            challenge.setAcceptanceCriteriaJson(writeJson(challengeDefinition.acceptanceCriteria(),
                    "challenge acceptance criteria", nodeDefinition.slug()));
            challenge.setHintsJson(writeJson(challengeDefinition.hints(), "challenge hints", nodeDefinition.slug()));
            challenge.setExpectedArtifactsJson(writeJson(challengeDefinition.expectedArtifacts(),
                    "challenge expected artifacts", nodeDefinition.slug()));
            challenge.setStarterRepositoryUrl(requireText(challengeDefinition.starterRepositoryUrl(),
                    "Challenge starter repository url"));
            challenge.setPassingScore(Objects.requireNonNull(challengeDefinition.passingScore(),
                    "Challenge passing score is required"));
            challenge.setTimeoutSeconds(Objects.requireNonNull(challengeDefinition.timeoutSeconds(),
                    "Challenge timeout is required"));
            challenge.setGraderImage(requireText(challengeDefinition.graderImage(), "Challenge grader image"));
            challenge.setGraderCommandJson(writeJson(challengeDefinition.graderCommand(),
                    "challenge grader command", nodeDefinition.slug()));
            challenge.setEvaluationEnabled(Objects.requireNonNull(challengeDefinition.evaluationEnabled(),
                    "Challenge evaluation enabled flag is required"));
            if (challenge.getCreatedBy() == null) {
                challenge.setCreatedBy(seedOwner.getUserId());
            }
            challenge.setUpdatedBy(seedOwner.getUserId());
            challengeRepository.save(challenge);
            syncedCount++;
        }

        challengeRepository.flush();
        return syncedCount;
    }

    private int syncPrerequisites(Map<String, SkillNode> nodesBySlug, List<NodeDefinition> nodeDefinitions) {
        int createdCount = 0;

        for (NodeDefinition nodeDefinition : nodeDefinitions) {
            SkillNode childNode = nodesBySlug.get(nodeDefinition.slug());

            for (String prerequisiteSlug : defaultList(nodeDefinition.prerequisites())) {
                SkillNode parentNode = nodesBySlug.get(prerequisiteSlug);
                if (parentNode == null) {
                    throw new IllegalStateException("Prerequisite slug not found in dataset: " + prerequisiteSlug);
                }
                if (parentNode.getNodeId().equals(childNode.getNodeId())) {
                    throw new IllegalStateException("Node cannot depend on itself: " + prerequisiteSlug);
                }

                NodePrerequisite prerequisite = new NodePrerequisite();
                prerequisite.setParentNode(parentNode);
                prerequisite.setChildNode(childNode);
                prerequisite.setRelationType(REQUIRED_RELATION_TYPE);
                nodePrerequisiteRepository.save(prerequisite);
                createdCount++;
            }
        }

        nodePrerequisiteRepository.flush();
        return createdCount;
    }

    private String writeJson(Object data, String label, String nodeSlug) {
        try {
            return objectMapper.writeValueAsString(data == null ? Map.of() : data);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize " + label + " for node: " + nodeSlug, exception);
        }
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(fieldName + " is required");
        }

        return value.trim();
    }

    private <T> List<T> defaultList(List<T> values) {
        return values == null ? List.of() : values;
    }

    private void requireNonEmptyTextList(List<String> values, String fieldName, String nodeSlug) {
        if (values == null || values.isEmpty() || values.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalStateException(fieldName + " must contain non-blank values: " + nodeSlug);
        }
    }

    public record RoadmapSeedResult(
            String roadmapSlug,
            int nodeCount,
            int resourceCount,
            int challengeCount,
            int prerequisiteCount) {
    }
}
