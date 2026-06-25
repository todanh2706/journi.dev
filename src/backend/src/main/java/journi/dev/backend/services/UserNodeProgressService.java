package journi.dev.backend.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import journi.dev.backend.dtos.responses.UserNodeProgressResponse;
import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;
import journi.dev.backend.entities.SkillNode;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserNodeProgress;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ResourceNotFoundException;
import journi.dev.backend.repositories.NodePrerequisiteRepository;
import journi.dev.backend.repositories.SkillNodeRepository;
import journi.dev.backend.repositories.UserNodeProgressRepository;

@Service
public class UserNodeProgressService {
    private final UserNodeProgressRepository userNodeProgressRepository;
    private final SkillNodeRepository skillNodeRepository;
    private final NodePrerequisiteRepository nodePrerequisiteRepository;

    public UserNodeProgressService(UserNodeProgressRepository userNodeProgressRepository,
            SkillNodeRepository skillNodeRepository,
            NodePrerequisiteRepository nodePrerequisiteRepository) {
        this.userNodeProgressRepository = userNodeProgressRepository;
        this.skillNodeRepository = skillNodeRepository;
        this.nodePrerequisiteRepository = nodePrerequisiteRepository;
    }

    @Transactional(readOnly = true)
    public List<UserNodeProgressResponse> getProgressForUser(User user) {
        requireUser(user);

        return userNodeProgressRepository.findByUser_UserId(user.getUserId()).stream()
                .sorted((left, right) -> Integer.compare(left.getNode().getOrderIndex(), right.getNode().getOrderIndex()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public UserNodeProgressResponse markNodeCompleted(User user, UUID nodeId) {
        requireUser(user);

        SkillNode node = skillNodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill node not found with id: " + nodeId));

        if (node.getNodeType() != NodeType.LESSON) {
            throw new BadRequestException("Only lesson nodes can be completed manually");
        }

        ProgressStatus computedStatus = getComputedStatus(user, node);
        if (computedStatus == ProgressStatus.LOCKED) {
            throw new BadRequestException("Cannot complete a locked skill node");
        }

        LocalDateTime now = LocalDateTime.now();

        UserNodeProgress progress = userNodeProgressRepository.findByUser_UserIdAndNode_NodeId(user.getUserId(), nodeId)
                .orElseGet(UserNodeProgress::new);

        progress.setUser(user);
        progress.setNode(node);
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setUnlockedAt(progress.getUnlockedAt() != null ? progress.getUnlockedAt() : now);
        progress.setCompletedAt(progress.getCompletedAt() != null ? progress.getCompletedAt() : now);
        progress.setLastAccessedAt(now);

        UserNodeProgress savedProgress = userNodeProgressRepository.save(progress);
        return toResponse(savedProgress);
    }

    @Transactional
    public UserNodeProgressResponse markAssessmentInProgress(User user, SkillNode node) {
        requireUser(user);
        requireAssessmentNode(node);

        ProgressStatus computedStatus = getComputedStatus(user, node);
        if (computedStatus == ProgressStatus.LOCKED) {
            throw new BadRequestException("Cannot start a locked skill node");
        }

        LocalDateTime now = LocalDateTime.now();
        UserNodeProgress progress = userNodeProgressRepository
                .findByUser_UserIdAndNode_NodeId(user.getUserId(), node.getNodeId())
                .orElseGet(UserNodeProgress::new);
        progress.setUser(user);
        progress.setNode(node);
        if (progress.getStatus() != ProgressStatus.COMPLETED) {
            progress.setStatus(ProgressStatus.IN_PROGRESS);
        }
        progress.setUnlockedAt(progress.getUnlockedAt() != null ? progress.getUnlockedAt() : now);
        progress.setLastAccessedAt(now);
        return toResponse(userNodeProgressRepository.save(progress));
    }

    @Transactional
    public UserNodeProgressResponse completeAssessmentFromPassedSubmission(User user, SkillNode node) {
        requireUser(user);
        requireAssessmentNode(node);

        LocalDateTime now = LocalDateTime.now();
        UserNodeProgress progress = userNodeProgressRepository
                .findByUser_UserIdAndNode_NodeId(user.getUserId(), node.getNodeId())
                .orElseGet(UserNodeProgress::new);
        progress.setUser(user);
        progress.setNode(node);
        progress.setStatus(ProgressStatus.COMPLETED);
        progress.setUnlockedAt(progress.getUnlockedAt() != null ? progress.getUnlockedAt() : now);
        progress.setCompletedAt(progress.getCompletedAt() != null ? progress.getCompletedAt() : now);
        progress.setLastAccessedAt(now);
        return toResponse(userNodeProgressRepository.save(progress));
    }

    @Transactional(readOnly = true)
    public ProgressStatus getComputedStatus(User user, SkillNode node) {
        return getComputedStatuses(user, List.of(node)).getOrDefault(node.getNodeId(), ProgressStatus.LOCKED);
    }

    @Transactional(readOnly = true)
    public Map<UUID, ProgressStatus> getComputedStatuses(User user, List<SkillNode> nodes) {
        requireUser(user);

        if (nodes.isEmpty()) {
            return Map.of();
        }

        Set<UUID> nodeIds = nodes.stream()
                .map(SkillNode::getNodeId)
                .collect(Collectors.toSet());

        Map<UUID, List<UUID>> prerequisiteIdsByChildNodeId = getPrerequisiteIdsByChildNodeId(nodeIds);
        Set<UUID> progressLookupNodeIds = new HashSet<>(nodeIds);
        prerequisiteIdsByChildNodeId.values().forEach(progressLookupNodeIds::addAll);

        Map<UUID, UserNodeProgress> progressByNodeId = getProgressByNodeId(user.getUserId(), progressLookupNodeIds);
        Set<UUID> completedNodeIds = progressByNodeId.values().stream()
                .filter(progress -> progress.getStatus() == ProgressStatus.COMPLETED)
                .map(progress -> progress.getNode().getNodeId())
                .collect(Collectors.toSet());

        return nodes.stream().collect(Collectors.toMap(SkillNode::getNodeId, node -> {
            UserNodeProgress savedProgress = progressByNodeId.get(node.getNodeId());

            if (savedProgress != null) {
                if (savedProgress.getStatus() == ProgressStatus.COMPLETED) {
                    return ProgressStatus.COMPLETED;
                }

                if (savedProgress.getStatus() == ProgressStatus.IN_PROGRESS) {
                    return ProgressStatus.IN_PROGRESS;
                }
            }

            List<UUID> prerequisiteIds = prerequisiteIdsByChildNodeId.getOrDefault(node.getNodeId(), List.of());
            boolean prerequisitesCompleted = prerequisiteIds.stream().allMatch(completedNodeIds::contains);

            return prerequisitesCompleted ? ProgressStatus.AVAILABLE : ProgressStatus.LOCKED;
        }));
    }

    private Map<UUID, UserNodeProgress> getProgressByNodeId(UUID userId, Collection<UUID> nodeIds) {
        return userNodeProgressRepository.findByUser_UserIdAndNode_NodeIdIn(userId, nodeIds).stream()
                .collect(Collectors.toMap(progress -> progress.getNode().getNodeId(), Function.identity()));
    }

    private Map<UUID, List<UUID>> getPrerequisiteIdsByChildNodeId(Collection<UUID> nodeIds) {
        return nodePrerequisiteRepository.findByChildNode_NodeIdIn(nodeIds).stream()
                .collect(Collectors.groupingBy(prerequisite -> prerequisite.getChildNode().getNodeId(),
                        Collectors.mapping(prerequisite -> prerequisite.getParentNode().getNodeId(),
                                Collectors.toList())));
    }

    private UserNodeProgressResponse toResponse(UserNodeProgress progress) {
        return new UserNodeProgressResponse(
                progress.getProgressId(),
                progress.getUser().getUserId(),
                progress.getNode().getNodeId(),
                progress.getNode().getRoadmap().getRoadmapId(),
                progress.getStatus(),
                progress.getUnlockedAt(),
                progress.getCompletedAt(),
                progress.getLastAccessedAt());
    }

    private void requireUser(User user) {
        if (user == null) {
            throw new BadRequestException("Authenticated user is required");
        }
    }

    private void requireAssessmentNode(SkillNode node) {
        if (node == null || (node.getNodeType() != NodeType.PRACTICE && node.getNodeType() != NodeType.PROJECT)) {
            throw new BadRequestException("Only practice and project nodes use assessment progress");
        }
    }
}
