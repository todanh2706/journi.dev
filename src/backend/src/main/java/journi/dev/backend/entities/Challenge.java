package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "challenge")
@SQLDelete(sql = "UPDATE challenge SET deleted_at = CURRENT_TIMESTAMP WHERE challenge_id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "challenge_id")
    private UUID challengeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private SkillNode node;

    @Column(length = 150, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 20)
    private String difficulty;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "acceptance_criteria_json", columnDefinition = "TEXT")
    private String acceptanceCriteriaJson;

    @Column(name = "hints_json", columnDefinition = "TEXT")
    private String hintsJson;

    @Column(name = "expected_artifacts_json", columnDefinition = "TEXT")
    private String expectedArtifactsJson;

    @Column(name = "starter_repository_url", length = 500)
    private String starterRepositoryUrl;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "timeout_seconds")
    private Integer timeoutSeconds;

    @Column(name = "grader_image", length = 500)
    private String graderImage;

    @Column(name = "grader_command_json", columnDefinition = "TEXT")
    private String graderCommandJson;

    @Column(name = "evaluation_enabled", nullable = false)
    private boolean evaluationEnabled;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public UUID getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(UUID challengeId) {
        this.challengeId = challengeId;
    }

    public SkillNode getNode() {
        return node;
    }

    public void setNode(SkillNode node) {
        this.node = node;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }

    public Boolean getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getAcceptanceCriteriaJson() {
        return acceptanceCriteriaJson;
    }

    public void setAcceptanceCriteriaJson(String acceptanceCriteriaJson) {
        this.acceptanceCriteriaJson = acceptanceCriteriaJson;
    }

    public String getHintsJson() {
        return hintsJson;
    }

    public void setHintsJson(String hintsJson) {
        this.hintsJson = hintsJson;
    }

    public String getExpectedArtifactsJson() {
        return expectedArtifactsJson;
    }

    public void setExpectedArtifactsJson(String expectedArtifactsJson) {
        this.expectedArtifactsJson = expectedArtifactsJson;
    }

    public String getStarterRepositoryUrl() {
        return starterRepositoryUrl;
    }

    public void setStarterRepositoryUrl(String starterRepositoryUrl) {
        this.starterRepositoryUrl = starterRepositoryUrl;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }

    public String getGraderImage() {
        return graderImage;
    }

    public void setGraderImage(String graderImage) {
        this.graderImage = graderImage;
    }

    public String getGraderCommandJson() {
        return graderCommandJson;
    }

    public void setGraderCommandJson(String graderCommandJson) {
        this.graderCommandJson = graderCommandJson;
    }

    public boolean isEvaluationEnabled() {
        return evaluationEnabled;
    }

    public void setEvaluationEnabled(boolean evaluationEnabled) {
        this.evaluationEnabled = evaluationEnabled;
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
}
