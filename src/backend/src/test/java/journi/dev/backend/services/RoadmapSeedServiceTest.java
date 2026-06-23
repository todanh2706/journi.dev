package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
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

import journi.dev.backend.entities.Challenge;
import journi.dev.backend.entities.LearningContent;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.repositories.ChallengeRepository;
import journi.dev.backend.repositories.LearningContentRepository;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserRepository;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class RoadmapSeedServiceTest {
    private static final String ROADMAP_SLUG = "backend-java-spring-boot-developer";

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

        assertThat(challengeRepository.findByNode_NodeIdIn(nodeIds)).hasSize(5).allSatisfy(challenge -> {
            assertThat(challenge.getTitle()).isNotBlank();
            assertThat(challenge.getDescription()).isNotBlank();
            assertThat(nodeIds).contains(challenge.getNode().getNodeId());
        });
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

        assertThat(secondRoadmap.getRoadmapId()).isEqualTo(firstRoadmap.getRoadmapId());
        assertThat(secondRunNodeIds).isEqualTo(firstRunNodeIds);
        assertThat(secondRunNodes.stream().collect(Collectors.toMap(SkillNode::getSlug, SkillNode::getContentJson)))
                .isEqualTo(firstContentBySlug);
        assertThat(learningRoadmapRepository.findAll()).hasSize(firstRoadmapCount);
        assertThat(userRepository.findAll()).hasSize(firstUserCount);
        assertThat(learningContentRepository.findByNode_NodeIdIn(secondNodeIds)).hasSize(firstResourceCount);
        assertThat(challengeRepository.findByNode_NodeIdIn(secondNodeIds)).hasSize(firstChallengeCount);
        assertThat(nodePrerequisiteRepository.findByChildNode_NodeIdIn(secondNodeIds)).hasSize(firstPrerequisiteCount);
    }
}
