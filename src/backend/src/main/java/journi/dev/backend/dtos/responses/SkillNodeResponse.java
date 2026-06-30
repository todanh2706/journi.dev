package journi.dev.backend.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import journi.dev.backend.entities.NodeType;
import journi.dev.backend.entities.ProgressStatus;

public class SkillNodeResponse {
    private UUID nodeId;
    private UUID roadmapId;
    private String title;
    private String slug;
    private Integer orderIndex;
    private NodeType nodeType;
    private String contentJson;
    private String summary;
    private String level;
    private Integer estimatedHours;
    private String note;
    private List<String> checklist;
    private List<LearningResourceResponse> learningResources;
    private ProgressStatus progressStatus;
    private Boolean isLocked;
    private Boolean hasRequiredChallenge;
    private String starterRepositoryUrl;
    private Boolean practiceSubmissionEnabled;
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

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public String getContentJson() {
        return contentJson;
    }

    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getChecklist() {
        return checklist;
    }

    public void setChecklist(List<String> checklist) {
        this.checklist = checklist;
    }

    public List<LearningResourceResponse> getLearningResources() {
        return learningResources;
    }

    public void setLearningResources(List<LearningResourceResponse> learningResources) {
        this.learningResources = learningResources;
    }

    public ProgressStatus getProgressStatus() {
        return progressStatus;
    }

    public void setProgressStatus(ProgressStatus progressStatus) {
        this.progressStatus = progressStatus;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    public Boolean getHasRequiredChallenge() {
        return hasRequiredChallenge;
    }

    public void setHasRequiredChallenge(Boolean hasRequiredChallenge) {
        this.hasRequiredChallenge = hasRequiredChallenge;
    }

    public String getStarterRepositoryUrl() {
        return starterRepositoryUrl;
    }

    public void setStarterRepositoryUrl(String starterRepositoryUrl) {
        this.starterRepositoryUrl = starterRepositoryUrl;
    }

    public Boolean getPracticeSubmissionEnabled() {
        return practiceSubmissionEnabled;
    }

    public void setPracticeSubmissionEnabled(Boolean practiceSubmissionEnabled) {
        this.practiceSubmissionEnabled = practiceSubmissionEnabled;
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
            NodeType nodeType,
            String contentJson, ProgressStatus progressStatus, Boolean isLocked, UUID createdBy, UUID updatedBy,
            LocalDateTime createdAt,
            LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.nodeId = nodeId;
        this.roadmapId = roadmapId;
        this.title = title;
        this.slug = slug;
        this.orderIndex = orderIndex;
        this.nodeType = nodeType;
        this.contentJson = contentJson;
        this.progressStatus = progressStatus;
        this.isLocked = isLocked;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }

    public SkillNodeResponse() {
    }
}
