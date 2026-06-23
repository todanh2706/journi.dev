package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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

import journi.dev.backend.dtos.responses.UserNodeProgressResponse;
import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserNodeProgress;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserNodeProgressRepository;

@ExtendWith(MockitoExtension.class)
class UserNodeProgressServiceTest {

    @Mock
    private UserNodeProgressRepository userNodeProgressRepository;

    @Mock
    private SkillNodeRepository skillNodeRepository;

    @Mock
    private NodePrerequisiteRepository nodePrerequisiteRepository;

    private UserNodeProgressService userNodeProgressService;

    @BeforeEach
    void setUp() {
        userNodeProgressService = new UserNodeProgressService(
                userNodeProgressRepository,
                skillNodeRepository,
                nodePrerequisiteRepository);
    }

    @DisplayName("[TEST] Node statuses are AVAILABLE for root nodes and LOCKED for dependent nodes")
    @Test
    void getComputedStatusesReturnsAvailableAndLockedStates() {
        User user = TestData.user();
        SkillNode fundamentals = TestData.node("programming-fundamentals", 1);
        SkillNode oop = TestData.node("oop-in-java", 2);
        NodePrerequisite prerequisite = TestData.prerequisite(fundamentals, oop);

        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), anyCollection()))
                .thenReturn(List.of());
        when(nodePrerequisiteRepository.findByChildNode_NodeIdIn(anyCollection()))
                .thenReturn(List.of(prerequisite));

        Map<UUID, ProgressStatus> statuses = userNodeProgressService.getComputedStatuses(user, List.of(fundamentals, oop));

        assertThat(statuses)
                .containsEntry(fundamentals.getNodeId(), ProgressStatus.AVAILABLE)
                .containsEntry(oop.getNodeId(), ProgressStatus.LOCKED);
    }

    @DisplayName("[TEST] Completed prerequisite unlocks the next node")
    @Test
    void getComputedStatusesUnlocksNodeAfterPrerequisiteCompletion() {
        User user = TestData.user();
        SkillNode fundamentals = TestData.node("programming-fundamentals", 1);
        SkillNode springCore = TestData.node("spring-core", 2);
        NodePrerequisite prerequisite = TestData.prerequisite(fundamentals, springCore);
        UserNodeProgress completedProgress = TestData.progress(user, fundamentals, ProgressStatus.COMPLETED);

        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), anyCollection()))
                .thenReturn(List.of(completedProgress));
        when(nodePrerequisiteRepository.findByChildNode_NodeIdIn(anyCollection()))
                .thenReturn(List.of(prerequisite));

        Map<UUID, ProgressStatus> statuses = userNodeProgressService.getComputedStatuses(user,
                List.of(fundamentals, springCore));

        assertThat(statuses)
                .containsEntry(fundamentals.getNodeId(), ProgressStatus.COMPLETED)
                .containsEntry(springCore.getNodeId(), ProgressStatus.AVAILABLE);
    }

    @DisplayName("[TEST] Completing an available node stores a COMPLETED progress record")
    @Test
    void markNodeCompletedStoresCompletionRecord() {
        User user = TestData.user();
        SkillNode fundamentals = TestData.node("programming-fundamentals", 1);

        when(skillNodeRepository.findById(fundamentals.getNodeId())).thenReturn(Optional.of(fundamentals));
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), anyCollection()))
                .thenReturn(List.of());
        when(nodePrerequisiteRepository.findByChildNode_NodeIdIn(anyCollection()))
                .thenReturn(List.of());
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeId(user.getUserId(), fundamentals.getNodeId()))
                .thenReturn(Optional.empty());
        when(userNodeProgressRepository.save(any(UserNodeProgress.class))).thenAnswer(invocation -> {
            UserNodeProgress progress = invocation.getArgument(0);
            progress.setProgressId(UUID.fromString("6d4b0290-8744-4672-bd12-fb0d9da7ab9f"));
            return progress;
        });

        UserNodeProgressResponse response = userNodeProgressService.markNodeCompleted(user, fundamentals.getNodeId());

        assertThat(response.getNodeId()).isEqualTo(fundamentals.getNodeId());
        assertThat(response.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
        assertThat(response.getCompletedAt()).isNotNull();

        ArgumentCaptor<UserNodeProgress> progressCaptor = ArgumentCaptor.forClass(UserNodeProgress.class);
        verify(userNodeProgressRepository).save(progressCaptor.capture());
        UserNodeProgress savedProgress = progressCaptor.getValue();

        assertThat(savedProgress.getUser()).isSameAs(user);
        assertThat(savedProgress.getNode()).isSameAs(fundamentals);
        assertThat(savedProgress.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
        assertThat(savedProgress.getUnlockedAt()).isNotNull();
        assertThat(savedProgress.getLastAccessedAt()).isNotNull();
    }

    @DisplayName("[TEST] Locked nodes cannot be completed")
    @Test
    void markNodeCompletedRejectsLockedNode() {
        User user = TestData.user();
        SkillNode fundamentals = TestData.node("programming-fundamentals", 1);
        SkillNode springBoot = TestData.node("spring-boot-basics", 2);
        NodePrerequisite prerequisite = TestData.prerequisite(fundamentals, springBoot);

        when(skillNodeRepository.findById(springBoot.getNodeId())).thenReturn(Optional.of(springBoot));
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), anyCollection()))
                .thenReturn(List.of());
        when(nodePrerequisiteRepository.findByChildNode_NodeIdIn(anyCollection()))
                .thenReturn(List.of(prerequisite));

        assertThatThrownBy(() -> userNodeProgressService.markNodeCompleted(user, springBoot.getNodeId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cannot complete a locked skill node");
    }

    @DisplayName("[TEST] A dependent lesson can be completed after its prerequisite is completed")
    @Test
    void markNodeCompletedLoadsPrerequisiteProgress() {
        User user = TestData.user();
        SkillNode fundamentals = TestData.node("programming-fundamentals", 1);
        SkillNode javaBasics = TestData.node("java-basics", 2);
        NodePrerequisite prerequisite = TestData.prerequisite(fundamentals, javaBasics);
        UserNodeProgress prerequisiteProgress = TestData.progress(user, fundamentals, ProgressStatus.COMPLETED);

        when(skillNodeRepository.findById(javaBasics.getNodeId())).thenReturn(Optional.of(javaBasics));
        when(nodePrerequisiteRepository.findByChildNode_NodeIdIn(anyCollection()))
                .thenReturn(List.of(prerequisite));
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), anyCollection()))
                .thenReturn(List.of(prerequisiteProgress));
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeId(user.getUserId(), javaBasics.getNodeId()))
                .thenReturn(Optional.empty());
        when(userNodeProgressRepository.save(any(UserNodeProgress.class))).thenAnswer(invocation -> {
            UserNodeProgress progress = invocation.getArgument(0);
            progress.setProgressId(UUID.randomUUID());
            return progress;
        });

        UserNodeProgressResponse response = userNodeProgressService.markNodeCompleted(user, javaBasics.getNodeId());

        assertThat(response.getStatus()).isEqualTo(ProgressStatus.COMPLETED);
        ArgumentCaptor<Collection<UUID>> nodeIdsCaptor = ArgumentCaptor.forClass(Collection.class);
        verify(userNodeProgressRepository)
                .findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), nodeIdsCaptor.capture());
        assertThat(nodeIdsCaptor.getValue())
                .containsExactlyInAnyOrder(javaBasics.getNodeId(), fundamentals.getNodeId());
    }

    @DisplayName("[TEST] Repeated completion preserves the first completion timestamp")
    @Test
    void markNodeCompletedIsIdempotent() {
        User user = TestData.user();
        SkillNode fundamentals = TestData.node("programming-fundamentals", 1);
        UserNodeProgress completedProgress = TestData.progress(user, fundamentals, ProgressStatus.COMPLETED);
        LocalDateTime firstCompletedAt = LocalDateTime.of(2026, 6, 20, 10, 15);
        completedProgress.setUnlockedAt(firstCompletedAt.minusMinutes(5));
        completedProgress.setCompletedAt(firstCompletedAt);

        when(skillNodeRepository.findById(fundamentals.getNodeId())).thenReturn(Optional.of(fundamentals));
        when(nodePrerequisiteRepository.findByChildNode_NodeIdIn(anyCollection())).thenReturn(List.of());
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(eq(user.getUserId()), anyCollection()))
                .thenReturn(List.of(completedProgress));
        when(userNodeProgressRepository.findByUser_UserIdAndNode_NodeId(user.getUserId(), fundamentals.getNodeId()))
                .thenReturn(Optional.of(completedProgress));
        when(userNodeProgressRepository.save(completedProgress)).thenReturn(completedProgress);

        UserNodeProgressResponse response = userNodeProgressService.markNodeCompleted(user, fundamentals.getNodeId());

        assertThat(response.getCompletedAt()).isEqualTo(firstCompletedAt);
        assertThat(completedProgress.getCompletedAt()).isEqualTo(firstCompletedAt);
    }

    @DisplayName("[TEST] Non-lesson nodes cannot be completed manually")
    @Test
    void markNodeCompletedRejectsNonLessonNode() {
        User user = TestData.user();
        SkillNode practice = TestData.node("collections-practice", 4);
        practice.setNodeType(NodeType.PRACTICE);

        when(skillNodeRepository.findById(practice.getNodeId())).thenReturn(Optional.of(practice));

        assertThatThrownBy(() -> userNodeProgressService.markNodeCompleted(user, practice.getNodeId()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Only lesson nodes can be completed manually");
        verify(userNodeProgressRepository, never()).save(any(UserNodeProgress.class));
    }

    private static final class TestData {
        private static final LearningRoadmap ROADMAP = roadmap();

        private TestData() {
        }

        static User user() {
            User user = new User();
            user.setUserId(UUID.fromString("21594c4d-2617-4dd8-a55e-b7e4124011d3"));
            user.setUsername("progress-user");
            user.setEmail("progress@example.com");
            user.setPasswordHash("encoded-password");
            user.setRole(UserRole.USER);
            user.setStatus(UserStatus.ACTIVE);
            user.setEnabled(true);
            return user;
        }

        static LearningRoadmap roadmap() {
            LearningRoadmap roadmap = new LearningRoadmap();
            roadmap.setRoadmapId(UUID.fromString("5697ff77-5d4a-46fd-a9ee-a0d4c60c8d1d"));
            roadmap.setTitle("Backend Java Spring Boot");
            roadmap.setSlug("backend-java-spring-boot");
            roadmap.setDescription("Core backend roadmap");
            roadmap.setVisibility("PRIVATE");
            roadmap.setIsDynamic(false);
            return roadmap;
        }

        static SkillNode node(String slug, int orderIndex) {
            SkillNode node = new SkillNode();
            node.setNodeId(UUID.nameUUIDFromBytes(slug.getBytes()));
            node.setRoadmap(ROADMAP);
            node.setTitle(slug.replace('-', ' '));
            node.setSlug(slug);
            node.setOrderIndex(orderIndex);
            node.setNodeType(NodeType.LESSON);
            node.setContentJson("{}");
            return node;
        }

        static NodePrerequisite prerequisite(SkillNode parentNode, SkillNode childNode) {
            NodePrerequisite prerequisite = new NodePrerequisite();
            prerequisite.setParentNode(parentNode);
            prerequisite.setChildNode(childNode);
            prerequisite.setRelationType("REQUIRED");
            return prerequisite;
        }

        static UserNodeProgress progress(User user, SkillNode node, ProgressStatus status) {
            UserNodeProgress progress = new UserNodeProgress();
            progress.setProgressId(UUID.randomUUID());
            progress.setUser(user);
            progress.setNode(node);
            progress.setStatus(status);
            return progress;
        }
    }
}
