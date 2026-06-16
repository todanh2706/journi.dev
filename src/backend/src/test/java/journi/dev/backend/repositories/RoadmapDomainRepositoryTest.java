package journi.dev.backend.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import journi.dev.backend.entities.LearningRoadmap;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserNodeProgress;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;

@ActiveProfiles("test")
@DataJpaTest
class RoadmapDomainRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningRoadmapRepository learningRoadmapRepository;

    @Autowired
    private SkillNodeRepository skillNodeRepository;

    @Autowired
    private NodePrerequisiteRepository nodePrerequisiteRepository;

    @Autowired
    private UserNodeProgressRepository userNodeProgressRepository;

    @DisplayName("[TEST] SkillNodeRepository returns nodes ordered by roadmap and order index")
    @Test
    void findByRoadmapIdReturnsOrderedNodes() {
        User owner = userRepository.saveAndFlush(TestData.user("roadmap-owner", "roadmap-owner@example.com"));
        LearningRoadmap roadmap = learningRoadmapRepository.saveAndFlush(
                TestData.roadmap(owner, "backend-roadmap", "Backend roadmap"));
        SkillNode laterNode = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "spring-core", 2));
        SkillNode firstNode = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "java-basics", 1));

        List<SkillNode> nodes = skillNodeRepository.findByRoadmap_RoadmapIdOrderByOrderIndexAsc(roadmap.getRoadmapId());

        assertThat(nodes)
                .extracting(SkillNode::getNodeId)
                .containsExactly(firstNode.getNodeId(), laterNode.getNodeId());
    }

    @DisplayName("[TEST] UserNodeProgressRepository filters progress by user and node ids")
    @Test
    void findByUserAndNodeIdsReturnsMatchingProgress() {
        User primaryUser = userRepository.saveAndFlush(TestData.user("primary-user", "primary@example.com"));
        User secondaryUser = userRepository.saveAndFlush(TestData.user("secondary-user", "secondary@example.com"));
        LearningRoadmap roadmap = learningRoadmapRepository.saveAndFlush(
                TestData.roadmap(primaryUser, "spring-roadmap", "Spring roadmap"));
        SkillNode javaNode = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "java-basics", 1));
        SkillNode springNode = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "spring-core", 2));

        userNodeProgressRepository.saveAndFlush(TestData.progress(primaryUser, javaNode, ProgressStatus.COMPLETED));
        userNodeProgressRepository.saveAndFlush(TestData.progress(primaryUser, springNode, ProgressStatus.IN_PROGRESS));
        userNodeProgressRepository.saveAndFlush(TestData.progress(secondaryUser, springNode, ProgressStatus.COMPLETED));

        List<UserNodeProgress> progressEntries = userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(
                primaryUser.getUserId(),
                List.of(javaNode.getNodeId(), springNode.getNodeId()));

        assertThat(progressEntries)
                .hasSize(2)
                .extracting(progress -> progress.getNode().getNodeId())
                .containsExactlyInAnyOrder(javaNode.getNodeId(), springNode.getNodeId());
    }

    @DisplayName("[TEST] User progress enforces a unique user-node pair")
    @Test
    void duplicateUserNodeProgressViolatesUniqueConstraint() {
        User owner = userRepository.saveAndFlush(TestData.user("unique-user", "unique@example.com"));
        LearningRoadmap roadmap = learningRoadmapRepository.saveAndFlush(
                TestData.roadmap(owner, "unique-roadmap", "Unique roadmap"));
        SkillNode node = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "docker-basics", 1));

        userNodeProgressRepository.saveAndFlush(TestData.progress(owner, node, ProgressStatus.IN_PROGRESS));

        assertThatThrownBy(() -> userNodeProgressRepository.saveAndFlush(
                TestData.progress(owner, node, ProgressStatus.COMPLETED)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @DisplayName("[TEST] NodePrerequisiteRepository finds prerequisite edges by child node ids")
    @Test
    void findByChildNodeIdsReturnsPrerequisites() {
        User owner = userRepository.saveAndFlush(TestData.user("graph-user", "graph@example.com"));
        LearningRoadmap roadmap = learningRoadmapRepository.saveAndFlush(
                TestData.roadmap(owner, "graph-roadmap", "Graph roadmap"));
        SkillNode parentNode = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "sql-basics", 1));
        SkillNode childNode = skillNodeRepository.saveAndFlush(TestData.node(roadmap, "jdbc-basics", 2));

        nodePrerequisiteRepository.saveAndFlush(TestData.prerequisite(parentNode, childNode));

        List<NodePrerequisite> prerequisites = nodePrerequisiteRepository.findByChildNode_NodeIdIn(
                List.of(childNode.getNodeId()));

        assertThat(prerequisites)
                .singleElement()
                .satisfies(prerequisite -> {
                    assertThat(prerequisite.getParentNode().getNodeId()).isEqualTo(parentNode.getNodeId());
                    assertThat(prerequisite.getChildNode().getNodeId()).isEqualTo(childNode.getNodeId());
                });
    }

    private static final class TestData {
        private TestData() {
        }

        static User user(String username, String email) {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash("encoded-password");
            user.setRole(UserRole.USER);
            user.setStatus(UserStatus.ACTIVE);
            user.setEnabled(true);
            user.setVerificationCode("verify-" + username);
            user.setVerificationExpiration(LocalDateTime.now().plusHours(1));
            return user;
        }

        static LearningRoadmap roadmap(User owner, String slug, String title) {
            LearningRoadmap roadmap = new LearningRoadmap();
            roadmap.setOwner(owner);
            roadmap.setTitle(title);
            roadmap.setSlug(slug);
            roadmap.setDescription("Roadmap description");
            roadmap.setVisibility("PRIVATE");
            roadmap.setIsDynamic(false);
            roadmap.setCreatedBy(owner.getUserId());
            return roadmap;
        }

        static SkillNode node(LearningRoadmap roadmap, String slug, int orderIndex) {
            SkillNode node = new SkillNode();
            node.setRoadmap(roadmap);
            node.setTitle(slug.replace('-', ' '));
            node.setSlug(slug);
            node.setOrderIndex(orderIndex);
            node.setNodeType(NodeType.LESSON);
            node.setContentJson("{}");
            node.setCreatedBy(roadmap.getOwner().getUserId());
            return node;
        }

        static UserNodeProgress progress(User user, SkillNode node, ProgressStatus status) {
            UserNodeProgress progress = new UserNodeProgress();
            progress.setUser(user);
            progress.setNode(node);
            progress.setStatus(status);
            progress.setUnlockedAt(LocalDateTime.now());
            progress.setLastAccessedAt(LocalDateTime.now());
            return progress;
        }

        static NodePrerequisite prerequisite(SkillNode parentNode, SkillNode childNode) {
            NodePrerequisite prerequisite = new NodePrerequisite();
            prerequisite.setParentNode(parentNode);
            prerequisite.setChildNode(childNode);
            prerequisite.setRelationType("REQUIRED");
            return prerequisite;
        }
    }
}
