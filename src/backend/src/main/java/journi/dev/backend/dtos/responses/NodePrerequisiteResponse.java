package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

public class NodePrerequisiteResponse {
    private UUID parentId;
    private UUID childNodeId;
    private String relationType;
    private LocalDateTime createdAt;

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }

    public UUID getChildNodeId() {
        return childNodeId;
    }

    public void setChildNodeId(UUID childNodeId) {
        this.childNodeId = childNodeId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public NodePrerequisiteResponse(UUID parentId, UUID childNodeId, String relationType, LocalDateTime createdAt) {
        this.parentId = parentId;
        this.childNodeId = childNodeId;
        this.relationType = relationType;
        this.createdAt = createdAt;
    }
}
