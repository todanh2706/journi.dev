package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.UUID;

import journi.dev.backend.entities.SkillNode;

public class SkillNodeResponse {
    private UUID nodeId;
    private UUID roadmapId;
    private String title;
    private String slug;
    private Integer orderIndex;
    private String nodeType;
    private String contentJson;
    private Boolean isLocked;
    private UUID createdBy;
    private UUID updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public SkillNodeResponse(UUID nodeId, UUID roadmapId, String title, String slug, Integer orderIndex,
            String nodeType,
            String contentJson, Boolean isLocked, UUID createdBy, UUID updatedBy, LocalDateTime createdAt,
            LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.nodeId = nodeId;
        this.roadmapId = roadmapId;
        this.title = title;
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.nodeType = nodeType;
        this.contentJson = contentJson;
        this.isLocked = isLocked;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public SkillNodeResponse(SkillNode skillNode) {
        this.nodeId = skillNode.getNodeId();
        this.roadmapId = skillNode.getRoadmapId();
        this.title = skillNode.getTitle();
        this.slug = skillNode.getSlug();
        this.orderIndex = skillNode.getOrderIndex();
        this.nodeType = skillNode.getNodeType();
        this.contentJson = skillNode.getContentJson();
        this.isLocked = skillNode.getIsLocked();
        this.createdBy = skillNode.getCreatedBy();
        this.updatedBy = skillNode.getUpdatedBy();
        this.createdAt = skillNode.getCreatedAt();
        this.updatedAt = skillNode.getUpdatedAt();
        this.deletedAt = skillNode.getDeletedAt();
    }
}
