package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import journi.dev.backend.entities.ProgressStatus;

public class UserNodeProgressResponse {
    private UUID progressId;
    private UUID userId;
    private UUID nodeId;
    private UUID roadmapId;
    private ProgressStatus status;
    private LocalDateTime unlockedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;

    public UserNodeProgressResponse() {
    }

    public UserNodeProgressResponse(UUID progressId, UUID userId, UUID nodeId, UUID roadmapId, ProgressStatus status,
            LocalDateTime unlockedAt, LocalDateTime completedAt, LocalDateTime lastAccessedAt) {
        this.progressId = progressId;
        this.userId = userId;
        this.nodeId = nodeId;
        this.roadmapId = roadmapId;
        this.status = status;
        this.unlockedAt = unlockedAt;
        this.completedAt = completedAt;
        this.lastAccessedAt = lastAccessedAt;
    }

    public UUID getProgressId() {
        return progressId;
    }

    public void setProgressId(UUID progressId) {
        this.progressId = progressId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getNodeId() {
        return nodeId;
    }

    public void setNodeId(UUID nodeId) {
        this.nodeId = nodeId;
    }

    public UUID getRoadmapId() {
        return roadmapId;
    }

    public void setRoadmapId(UUID roadmapId) {
        this.roadmapId = roadmapId;
    }

    public ProgressStatus getStatus() {
        return status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }

    public LocalDateTime getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(LocalDateTime unlockedAt) {
        this.unlockedAt = unlockedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }
}
