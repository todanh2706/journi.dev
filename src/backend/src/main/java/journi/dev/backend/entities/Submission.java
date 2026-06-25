package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

@Entity
@Table(name = "submission", uniqueConstraints = {
        @UniqueConstraint(name = "uk_submission_user_challenge_commit", columnNames = {
                "user_id", "challenge_id", "commit_hash"
        })
}, indexes = {
        @Index(name = "idx_submission_user_challenge_attempt", columnList = "user_id, challenge_id, attempt_number"),
        @Index(name = "idx_submission_status_lease", columnList = "status, evaluation_lease_until")
})
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "submission_id")
    private UUID submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Column(name = "github_repo_id")
    private UUID legacyGithubRepoId;

    @Column(name = "repository_url", length = 500)
    private String repositoryUrl;

    @Column(name = "branch_name", length = 100)
    private String branchName;

    @Column(name = "commit_hash", length = 100)
    private String commitHash;

    @Column(name = "attempt_number")
    private Integer attemptNumber;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SubmissionStatus status;

    private Integer score;

    @Column(name = "result_summary", length = 500)
    private String resultSummary;

    @Column(name = "feedback_json", columnDefinition = "TEXT")
    private String feedbackJson;

    @Column(name = "output_excerpt", columnDefinition = "TEXT")
    private String outputExcerpt;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_category", length = 40)
    private SubmissionFailureCategory failureCategory;

    @Column(name = "evaluation_lease_until")
    private LocalDateTime evaluationLeaseUntil;

    @Column(name = "evaluation_started_at")
    private LocalDateTime evaluationStartedAt;

    @Column(name = "evaluation_completed_at")
    private LocalDateTime evaluationCompletedAt;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false, updatable = false)
    private LocalDateTime submittedAt;

    @Version
    private long version;

    public boolean isEvaluationEligible() {
        return challenge != null
                && repositoryUrl != null && !repositoryUrl.isBlank()
                && branchName != null && !branchName.isBlank()
                && commitHash != null && !commitHash.isBlank()
                && attemptNumber != null
                && status != null;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public UUID getLegacyGithubRepoId() {
        return legacyGithubRepoId;
    }

    public void setLegacyGithubRepoId(UUID legacyGithubRepoId) {
        this.legacyGithubRepoId = legacyGithubRepoId;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getResultSummary() {
        return resultSummary;
    }

    public void setResultSummary(String resultSummary) {
        this.resultSummary = resultSummary;
    }

    public String getFeedbackJson() {
        return feedbackJson;
    }

    public void setFeedbackJson(String feedbackJson) {
        this.feedbackJson = feedbackJson;
    }

    public String getOutputExcerpt() {
        return outputExcerpt;
    }

    public void setOutputExcerpt(String outputExcerpt) {
        this.outputExcerpt = outputExcerpt;
    }

    public SubmissionFailureCategory getFailureCategory() {
        return failureCategory;
    }

    public void setFailureCategory(SubmissionFailureCategory failureCategory) {
        this.failureCategory = failureCategory;
    }

    public LocalDateTime getEvaluationLeaseUntil() {
        return evaluationLeaseUntil;
    }

    public void setEvaluationLeaseUntil(LocalDateTime evaluationLeaseUntil) {
        this.evaluationLeaseUntil = evaluationLeaseUntil;
    }

    public LocalDateTime getEvaluationStartedAt() {
        return evaluationStartedAt;
    }

    public void setEvaluationStartedAt(LocalDateTime evaluationStartedAt) {
        this.evaluationStartedAt = evaluationStartedAt;
    }

    public LocalDateTime getEvaluationCompletedAt() {
        return evaluationCompletedAt;
    }

    public void setEvaluationCompletedAt(LocalDateTime evaluationCompletedAt) {
        this.evaluationCompletedAt = evaluationCompletedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public long getVersion() {
        return version;
    }
}
