package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import journi.dev.backend.dtos.responses.SkillNodeResponse;
import journi.dev.backend.entities.LearningContent;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.mappers.SkillNodeMapper;
import journi.dev.backend.repositories.LearningContentRepository;
import journi.dev.backend.repositories.LearningRoadmapRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class SkillNodeServiceTest {

    @Mock
    private SkillNodeRepository skillNodeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LearningRoadmapRepository roadmapRepository;

    @Mock
    private SkillNodeMapper skillNodeMapper;

    @Mock
    private UserNodeProgressService userNodeProgressService;

    @Mock
    private LearningContentRepository learningContentRepository;

    private SkillNodeService skillNodeService;

    @BeforeEach
    void setUp() {
        skillNodeService = new SkillNodeService(
                skillNodeRepository,
                userRepository,
                roadmapRepository,
                skillNodeMapper,
                userNodeProgressService,
                learningContentRepository,
                new ObjectMapper());

        when(skillNodeMapper.toResponse(any(SkillNode.class))).thenAnswer(invocation -> {
            SkillNode node = invocation.getArgument(0);
            SkillNodeResponse response = new SkillNodeResponse();
            response.setNodeId(node.getNodeId());
            response.setRoadmapId(node.getRoadmap().getRoadmapId());
            response.setTitle(node.getTitle());
            response.setSlug(node.getSlug());
            response.setOrderIndex(node.getOrderIndex());
            response.setNodeType(node.getNodeType());
            response.setContentJson(node.getContentJson());
            return response;
        });
    }

    @DisplayName("[TEST] Node list maps all progress states and redacts locked learning details")
    @Test
    void getNodesByRoadmapMapsProgressGatedLearningDetailsWithOneResourceQuery() {
        User user = user();
        LearningRoadmap roadmap = roadmap();
        SkillNode locked = node(roadmap, "locked", 1);
        SkillNode available = node(roadmap, "available", 2);
        SkillNode inProgress = node(roadmap, "in-progress", 3);
        SkillNode completed = node(roadmap, "completed", 4);
        List<SkillNode> nodes = List.of(locked, available, inProgress, completed);
        LearningContent availableResource = resource(available, "Java Docs", "https://dev.java/learn/");

        when(skillNodeRepository.findByRoadmap_RoadmapIdOrderByOrderIndexAsc(roadmap.getRoadmapId()))
                .thenReturn(nodes);
        when(userNodeProgressService.getComputedStatuses(user, nodes)).thenReturn(Map.of(
                locked.getNodeId(), ProgressStatus.LOCKED,
                available.getNodeId(), ProgressStatus.AVAILABLE,
                inProgress.getNodeId(), ProgressStatus.IN_PROGRESS,
                completed.getNodeId(), ProgressStatus.COMPLETED));
        when(learningContentRepository.findByNode_NodeIdIn(anyCollection()))
                .thenReturn(List.of(availableResource));

        List<SkillNodeResponse> responses = skillNodeService.getNodesByRoadmap(roadmap.getRoadmapId(), user);

        assertThat(responses).extracting(SkillNodeResponse::getProgressStatus)
                .containsExactly(
                        ProgressStatus.LOCKED,
                        ProgressStatus.AVAILABLE,
                        ProgressStatus.IN_PROGRESS,
                        ProgressStatus.COMPLETED);

        SkillNodeResponse lockedResponse = responses.get(0);
        assertThat(lockedResponse.getIsLocked()).isTrue();
        assertThat(lockedResponse.getContentJson()).isNull();
        assertThat(lockedResponse.getSummary()).isNull();
        assertThat(lockedResponse.getLevel()).isNull();
        assertThat(lockedResponse.getEstimatedHours()).isNull();
        assertThat(lockedResponse.getNote()).isNull();
        assertThat(lockedResponse.getChecklist()).isEmpty();
        assertThat(lockedResponse.getLearningResources()).isEmpty();

        SkillNodeResponse availableResponse = responses.get(1);
        assertThat(availableResponse.getIsLocked()).isFalse();
        assertThat(availableResponse.getSummary()).isEqualTo("Learn available");
        assertThat(availableResponse.getLevel()).isEqualTo("BEGINNER");
        assertThat(availableResponse.getEstimatedHours()).isEqualTo(8);
        assertThat(availableResponse.getNote()).isEqualTo("Build an exercise.");
        assertThat(availableResponse.getChecklist()).containsExactly("Read the guide", "Build the exercise");
        assertThat(availableResponse.getLearningResources()).singleElement().satisfies(resource -> {
            assertThat(resource.getTitle()).isEqualTo("Java Docs");
            assertThat(resource.getSourceType()).isEqualTo("DOCUMENTATION");
            assertThat(resource.getSourceUrl()).isEqualTo("https://dev.java/learn/");
            assertThat(resource.getDescription()).isEqualTo("Read the relevant sections.");
        });
        assertThat(responses.subList(2, 4)).allSatisfy(response -> {
            assertThat(response.getIsLocked()).isFalse();
            assertThat(response.getSummary()).isNotBlank();
        });

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<UUID>> nodeIdsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(learningContentRepository).findByNode_NodeIdIn(nodeIdsCaptor.capture());
        assertThat(nodeIdsCaptor.getValue())
                .containsExactlyInAnyOrder(available.getNodeId(), inProgress.getNodeId(), completed.getNodeId())
                .doesNotContain(locked.getNodeId());
    }

    @DisplayName("[TEST] Individual available node tolerates malformed content JSON")
    @Test
    void getNodeByIdReturnsEmptyTypedDetailsForMalformedContent() {
        User user = user();
        SkillNode node = node(roadmap(), "malformed", 1);
        node.setContentJson("{not-json");

        when(skillNodeRepository.findById(node.getNodeId())).thenReturn(Optional.of(node));
        when(userNodeProgressService.getComputedStatuses(user, List.of(node)))
                .thenReturn(Map.of(node.getNodeId(), ProgressStatus.AVAILABLE));
        when(learningContentRepository.findByNode_NodeIdIn(anyCollection())).thenReturn(List.of());

        SkillNodeResponse response = skillNodeService.getNodeById(node.getNodeId(), user);

        assertThat(response.getProgressStatus()).isEqualTo(ProgressStatus.AVAILABLE);
        assertThat(response.getIsLocked()).isFalse();
        assertThat(response.getSummary()).isNull();
        assertThat(response.getLevel()).isNull();
        assertThat(response.getEstimatedHours()).isNull();
        assertThat(response.getNote()).isNull();
        assertThat(response.getChecklist()).isEmpty();
        assertThat(response.getLearningResources()).isEmpty();
    }

    private static SkillNode node(LearningRoadmap roadmap, String slug, int orderIndex) {
        SkillNode node = new SkillNode();
        node.setNodeId(UUID.randomUUID());
        node.setRoadmap(roadmap);
        node.setTitle("Node " + slug);
        node.setSlug(slug);
        node.setOrderIndex(orderIndex);
        node.setNodeType(NodeType.LESSON);
        node.setContentJson("""
                {
                  "summary": "Learn %s",
                  "level": "BEGINNER",
                  "estimatedHours": 8,
                  "note": "Build an exercise.",
                  "checklist": ["Read the guide", "Build the exercise"]
                }
                """.formatted(slug));
        return node;
    }

    private static LearningContent resource(SkillNode node, String title, String url) {
        LearningContent resource = new LearningContent();
        resource.setNode(node);
        resource.setTitle(title);
        resource.setSourceType("DOCUMENTATION");
        resource.setSourceUrl(url);
        resource.setContentBody("Read the relevant sections.");
        return resource;
    }

    private static LearningRoadmap roadmap() {
        LearningRoadmap roadmap = new LearningRoadmap();
        roadmap.setRoadmapId(UUID.randomUUID());
        roadmap.setTitle("Backend Java Spring Boot Developer");
        roadmap.setSlug("backend-java-spring-boot-developer");
        return roadmap;
    }

    private static User user() {
        User user = new User();
        user.setUserId(UUID.randomUUID());
        return user;
    }
}
