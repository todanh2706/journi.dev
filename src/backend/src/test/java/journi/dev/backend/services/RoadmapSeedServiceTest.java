package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.LearningContent;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.Submission;
import journi.dev.backend.entities.SubmissionStatus;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.LearningContentRepository;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.SubmissionRepository;
import journi.dev.backend.repositories.UserRepository;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class RoadmapSeedServiceTest {
    private static final String ROADMAP_SLUG = "backend-java-spring-boot-developer";
    private static final Map<String, String> EXPECTED_STARTER_REPOSITORIES = Map.ofEntries(
            Map.entry("collections-and-generics",
                    "https://github.com/todanh2706/journi-practice-collections-and-generics"),
            Map.entry("jdbc-basics", "https://github.com/todanh2706/journi-practice-jdbc-basics"),
            Map.entry("rest-api-development",
                    "https://github.com/todanh2706/journi-practice-rest-api-development"),
            Map.entry("spring-data-jpa", "https://github.com/todanh2706/journi-practice-spring-data-jpa"),
            Map.entry("spring-security-and-jwt",
                    "https://github.com/todanh2706/journi-practice-spring-security-and-jwt"),
            Map.entry("testing-basics", "https://github.com/todanh2706/journi-practice-testing-basics"),
            Map.entry("docker-basics", "https://github.com/todanh2706/journi-practice-docker-basics"),
            Map.entry("deployment-basics", "https://github.com/todanh2706/journi-practice-deployment-basics"));

    @Autowired
    private RoadmapSeedService roadmapSeedService;

    @Autowired
    private LearningRoadmapRepository learningRoadmapRepository;

    @Autowired
    private SkillNodeRepository skillNodeRepository;

    @Autowired
    private NodePrerequisiteRepository nodePrerequisiteRepository;

    @Autowired
    private LearningContentRepository learningContentRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("[TEST] Roadmap seeder creates the expected roadmap graph and relational content")
    @Test
    void seedConfiguredRoadmapsCreatesExpectedGraph() throws IOException {
        roadmapSeedService.seedConfiguredRoadmaps();

        LearningRoadmap roadmap = learningRoadmapRepository.findBySlug(ROADMAP_SLUG).orElseThrow();
        List<SkillNode> nodes = skillNodeRepository.findByRoadmap_RoadmapIdOrderByOrderIndexAsc(roadmap.getRoadmapId());
        List<UUID> nodeIds = nodes.stream().map(SkillNode::getNodeId).toList();
        Map<String, SkillNode> nodesBySlug = nodes.stream()
                .collect(Collectors.toMap(SkillNode::getSlug, Function.identity()));

        assertThat(roadmap.getTitle()).isEqualTo("Backend Java Spring Boot Developer");
        assertThat(roadmap.getVisibility()).isEqualTo("PUBLIC");
        assertThat(roadmap.getIsDynamic()).isFalse();
        assertThat(roadmap.getOwner().getUsername()).isEqualTo("system_roadmap_seed");
        assertThat(nodes)
                .hasSize(14)
                .extracting(SkillNode::getSlug)
                .containsExactly(
                        "programming-fundamentals",
                        "java-basics",
                        "oop-in-java",
                        "collections-and-generics",
                        "sql-basics",
                        "jdbc-basics",
                        "spring-core",
                        "spring-boot-basics",
                        "rest-api-development",
                        "spring-data-jpa",
                        "spring-security-and-jwt",
                        "testing-basics",
                        "docker-basics",
                        "deployment-basics");
        assertThat(nodes)
                .extracting(SkillNode::getOrderIndex)
                .containsExactlyElementsOf(java.util.stream.IntStream.rangeClosed(1, 14).boxed().toList());

        for (SkillNode node : nodes) {
            JsonNode content = objectMapper.readTree(node.getContentJson());
            assertThat(content.path("summary").asText()).isNotBlank();
            assertThat(content.path("level").asText()).isIn("BEGINNER", "INTERMEDIATE");
            assertThat(content.path("estimatedHours").asInt()).isPositive();
            assertThat(content.path("note").asText()).isNotBlank();
            assertThat(content.path("checklist")).hasSizeGreaterThanOrEqualTo(3);
            assertThat(content.path("checklist")).allSatisfy(item -> assertThat(item.asText()).isNotBlank());
        }

        JsonNode restApiContent = objectMapper.readTree(nodesBySlug.get("rest-api-development").getContentJson());
        assertThat(restApiContent.get("summary").asText()).contains("HTTP APIs");
        assertThat(restApiContent.get("level").asText()).isEqualTo("INTERMEDIATE");
        assertThat(restApiContent.get("estimatedHours").asInt()).isEqualTo(16);
        assertThat(restApiContent.get("checklist")).hasSize(3);

        List<LearningContent> restApiResources = learningContentRepository
                .findByNode_NodeId(nodesBySlug.get("rest-api-development").getNodeId());
        List<Challenge> restApiChallenges = challengeRepository
                .findByNode_NodeId(nodesBySlug.get("rest-api-development").getNodeId());
        List<NodePrerequisite> prerequisites = nodePrerequisiteRepository.findByChildNode_NodeIdIn(nodeIds);
        List<LearningContent> allResources = learningContentRepository.findByNode_NodeIdIn(nodeIds);
        Map<UUID, List<LearningContent>> resourcesByNodeId = allResources.stream()
                .collect(Collectors.groupingBy(resource -> resource.getNode().getNodeId()));

        assertThat(restApiResources).hasSize(2);
        assertThat(restApiChallenges)
                .singleElement()
                .satisfies(challenge -> assertThat(challenge.getTitle()).isEqualTo("Build a Book Catalog REST API"));
        assertThat(prerequisites).hasSize(15);
        assertThat(prerequisites).allSatisfy(prerequisite -> {
            assertThat(nodeIds).contains(prerequisite.getParentNode().getNodeId());
            assertThat(nodeIds).contains(prerequisite.getChildNode().getNodeId());
            assertThat(prerequisite.getRelationType()).isEqualTo("REQUIRED");
            assertThat(prerequisite.getParentNode().getOrderIndex())
                    .isLessThan(prerequisite.getChildNode().getOrderIndex());
        });

        assertThat(allResources).hasSize(28).allSatisfy(resource -> {
            assertThat(resource.getTitle()).isNotBlank();
            assertThat(resource.getSourceType()).isNotBlank();
            assertThat(resource.getSourceUrl()).startsWith("https://");
            assertThat(resource.getContentBody()).isNotBlank();
        });
        assertThat(resourcesByNodeId).hasSize(14);
        assertThat(nodes).allSatisfy(node -> assertThat(resourcesByNodeId.get(node.getNodeId()))
                .hasSizeGreaterThanOrEqualTo(2));

        List<Challenge> challenges = challengeRepository.findByNode_NodeIdIn(nodeIds);
        Map<String, String> starterRepositoriesBySlug = challenges.stream()
                .collect(Collectors.toMap(challenge -> challenge.getNode().getSlug(),
                        Challenge::getStarterRepositoryUrl));
        assertThat(challenges).hasSize(8).allSatisfy(challenge -> {
            assertThat(challenge.getTitle()).isNotBlank();
            assertThat(challenge.getDescription()).isNotBlank();
            assertThat(challenge.getInstructions()).isNotBlank();
            assertThat(challenge.getIsRequired()).isTrue();
            assertThat(challenge.getMaxScore()).isPositive();
            assertThat(challenge.getPassingScore()).isBetween(1, challenge.getMaxScore());
            assertThat(challenge.getTimeoutSeconds()).isBetween(10, 900);
            assertThat(challenge.getStarterRepositoryUrl()).matches("https://github\\.com/[^/]+/[^/]+$");
            assertThat(challenge.getGraderImage()).matches("^[^\\s]+@sha256:[0-9a-f]{64}$");
            assertJsonArrayNotEmpty(challenge.getAcceptanceCriteriaJson());
            assertJsonArrayNotEmpty(challenge.getHintsJson());
            assertJsonArrayNotEmpty(challenge.getExpectedArtifactsJson());
            assertJsonArrayNotEmpty(challenge.getGraderCommandJson());
            assertThat(nodeIds).contains(challenge.getNode().getNodeId());
        });
        assertThat(starterRepositoriesBySlug)
                .containsExactlyInAnyOrderEntriesOf(EXPECTED_STARTER_REPOSITORIES);
        assertThat(starterRepositoriesBySlug.values())
                .doesNotHaveDuplicates()
                .allSatisfy(starterRepositoryUrl -> assertThat(starterRepositoryUrl)
                        .startsWith("https://github.com/todanh2706/journi-practice-")
                        .doesNotEndWith(".git")
                        .doesNotContain("journi.dev"));
        assertThat(challenges)
                .extracting(challenge -> challenge.getNode().getSlug())
                .containsExactlyInAnyOrder(
                        "collections-and-generics",
                        "jdbc-basics",
                        "rest-api-development",
                        "spring-data-jpa",
                        "spring-security-and-jwt",
                        "testing-basics",
                        "docker-basics",
                        "deployment-basics");
        assertThat(challenges).allSatisfy(challenge -> assertThat(challenge.getNode().getNodeType())
                .isIn(NodeType.PRACTICE, NodeType.PROJECT));
        assertThat(nodes.stream().filter(node -> node.getNodeType() == NodeType.LESSON))
                .allSatisfy(node -> assertThat(challengeRepository.findByNode_NodeId(node.getNodeId())).isEmpty());
        assertThat(challenges.stream().filter(Challenge::isEvaluationEnabled))
                .singleElement()
                .satisfies(challenge -> assertThat(challenge.getNode().getSlug())
                        .isEqualTo("collections-and-generics"));
        assertThat(challenges.stream().filter(challenge -> !challenge.isEvaluationEnabled())).hasSize(7);
    }

    @DisplayName("[TEST] Roadmap seeder is idempotent on rerun")
    @Test
    void seedConfiguredRoadmapsIsIdempotent() {
        roadmapSeedService.seedConfiguredRoadmaps();

        LearningRoadmap firstRoadmap = learningRoadmapRepository.findBySlug(ROADMAP_SLUG).orElseThrow();
        List<SkillNode> firstRunNodes = skillNodeRepository
                .findByRoadmap_RoadmapIdOrderByOrderIndexAsc(firstRoadmap.getRoadmapId());
        List<UUID> nodeIds = firstRunNodes.stream().map(SkillNode::getNodeId).toList();
        Map<String, UUID> firstRunNodeIds = firstRunNodes.stream()
                .collect(Collectors.toMap(SkillNode::getSlug, SkillNode::getNodeId));
        Map<String, UUID> firstRunChallengeIds = challengeRepository.findByNode_NodeIdIn(nodeIds).stream()
                .collect(Collectors.toMap(challenge -> challenge.getNode().getSlug(), Challenge::getChallengeId));

        User learner = new User();
        learner.setUsername("seed-idempotency-learner");
        learner.setEmail("seed-idempotency@example.com");
        learner.setPasswordHash("encoded-password");
        learner.setRole(UserRole.USER);
        learner.setStatus(UserStatus.ACTIVE);
        learner.setEnabled(true);
        learner = userRepository.saveAndFlush(learner);

        Challenge pilotChallenge = challengeRepository.findById(
                firstRunChallengeIds.get("collections-and-generics")).orElseThrow();
        Submission submission = new Submission();
        submission.setUser(learner);
        submission.setChallenge(pilotChallenge);
        submission.setRepositoryUrl("https://github.com/example/library-catalog");
        submission.setBranchName("main");
        submission.setCommitHash("a".repeat(40));
        submission.setAttemptNumber(1);
        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission = submissionRepository.saveAndFlush(submission);
        UUID submissionId = submission.getSubmissionId();

        int firstRoadmapCount = learningRoadmapRepository.findAll().size();
        int firstUserCount = userRepository.findAll().size();
        int firstResourceCount = learningContentRepository.findByNode_NodeIdIn(nodeIds).size();
        int firstChallengeCount = challengeRepository.findByNode_NodeIdIn(nodeIds).size();
        int firstPrerequisiteCount = nodePrerequisiteRepository.findByChildNode_NodeIdIn(nodeIds).size();
        Map<String, String> firstContentBySlug = firstRunNodes.stream()
                .collect(Collectors.toMap(SkillNode::getSlug, SkillNode::getContentJson));

        roadmapSeedService.seedConfiguredRoadmaps();

        LearningRoadmap secondRoadmap = learningRoadmapRepository.findBySlug(ROADMAP_SLUG).orElseThrow();
        List<SkillNode> secondRunNodes = skillNodeRepository
                .findByRoadmap_RoadmapIdOrderByOrderIndexAsc(secondRoadmap.getRoadmapId());
        List<UUID> secondNodeIds = secondRunNodes.stream().map(SkillNode::getNodeId).toList();
        Map<String, UUID> secondRunNodeIds = secondRunNodes.stream()
                .collect(Collectors.toMap(SkillNode::getSlug, SkillNode::getNodeId));
        Map<String, UUID> secondRunChallengeIds = challengeRepository.findByNode_NodeIdIn(secondNodeIds).stream()
                .collect(Collectors.toMap(challenge -> challenge.getNode().getSlug(), Challenge::getChallengeId));

        assertThat(secondRoadmap.getRoadmapId()).isEqualTo(firstRoadmap.getRoadmapId());
        assertThat(secondRunNodeIds).isEqualTo(firstRunNodeIds);
        assertThat(secondRunChallengeIds).isEqualTo(firstRunChallengeIds);
        assertThat(submissionRepository.findById(submissionId))
                .isPresent()
                .get()
                .extracting(saved -> saved.getChallenge().getChallengeId())
                .isEqualTo(pilotChallenge.getChallengeId());
        assertThat(secondRunNodes.stream().collect(Collectors.toMap(SkillNode::getSlug, SkillNode::getContentJson)))
                .isEqualTo(firstContentBySlug);
        assertThat(learningRoadmapRepository.findAll()).hasSize(firstRoadmapCount);
        assertThat(userRepository.findAll()).hasSize(firstUserCount);
        assertThat(learningContentRepository.findByNode_NodeIdIn(secondNodeIds)).hasSize(firstResourceCount);
        assertThat(challengeRepository.findByNode_NodeIdIn(secondNodeIds)).hasSize(firstChallengeCount);
        assertThat(nodePrerequisiteRepository.findByChildNode_NodeIdIn(secondNodeIds)).hasSize(firstPrerequisiteCount);
    }

    @DisplayName("[TEST] Roadmap seeder rejects duplicate starter repository mappings")
    @Test
    void seedConfiguredRoadmapsRejectsDuplicateStarterRepositoryMappings() throws IOException {
        Path dataset = writeDatasetWithStarterRepositoryOverrides(Map.of(
                "jdbc-basics", EXPECTED_STARTER_REPOSITORIES.get("collections-and-generics")));

        assertThatThrownBy(() -> roadmapSeedService.seedFromLocation(dataset.toUri().toString()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Duplicate challenge starter repository");
    }

    @DisplayName("[TEST] Roadmap seeder rejects the main application repository as a starter repository")
    @Test
    void seedConfiguredRoadmapsRejectsMainSourceRepositoryPlaceholder() throws IOException {
        Path dataset = writeDatasetWithStarterRepositoryOverrides(Map.of(
                "jdbc-basics", "https://github.com/todanh2706/journi.dev"));

        assertThatThrownBy(() -> roadmapSeedService.seedFromLocation(dataset.toUri().toString()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("main Journi.dev source repository");
    }

    @DisplayName("[TEST] Roadmap seeder rejects malformed starter repository URLs")
    @Test
    void seedConfiguredRoadmapsRejectsMalformedStarterRepositoryUrls() throws IOException {
        Path dataset = writeDatasetWithStarterRepositoryOverrides(Map.of(
                "jdbc-basics", "https://github.com/todanh2706/journi-practice-jdbc-basics/tree/main"));

        assertThatThrownBy(() -> roadmapSeedService.seedFromLocation(dataset.toUri().toString()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must be a GitHub repository URL");
    }

    private void assertJsonArrayNotEmpty(String json) {
        try {
            JsonNode value = objectMapper.readTree(json);
            assertThat(value.isArray()).isTrue();
            assertThat(value.size()).isGreaterThanOrEqualTo(1);
        } catch (IOException exception) {
            throw new AssertionError("Expected a valid JSON array", exception);
        }
    }

    private Path writeDatasetWithStarterRepositoryOverrides(Map<String, String> overrides) throws IOException {
        JsonNode dataset;
        try (InputStream inputStream = RoadmapSeedServiceTest.class
                .getResourceAsStream("/seed-data/backend-java-spring-roadmap.json")) {
            if (inputStream == null) {
                throw new IllegalStateException("Seed dataset fixture is missing");
            }
            dataset = objectMapper.readTree(inputStream);
        }

        for (JsonNode node : dataset.path("nodes")) {
            String slug = node.path("slug").asText();
            if (overrides.containsKey(slug) && node.path("challenge") instanceof ObjectNode challenge) {
                challenge.put("starterRepositoryUrl", overrides.get(slug));
            }
        }

        Path datasetFile = Files.createTempFile("journi-roadmap-seed-", ".json");
        objectMapper.writeValue(datasetFile.toFile(), dataset);
        return datasetFile;
    }
}
