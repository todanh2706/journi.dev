package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "submission")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "submission_id")
    private UUID submissionId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "challenge_id")
    private UUID challengeId;

    @Column(name = "github_repo_id")
    private UUID githubRepoId;

    @Column(name = "branch_name", length = 100)
    private String branchName;

    @Column(name = "commit_hash", length = 100)
    private String commitHash;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(length = 30)
    private String status;

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(UUID challengeId) {
        this.challengeId = challengeId;
    }

    public UUID getGithubRepoId() {
        return githubRepoId;
    }

    public void setGithubRepoId(UUID githubRepoId) {
        this.githubRepoId = githubRepoId;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
