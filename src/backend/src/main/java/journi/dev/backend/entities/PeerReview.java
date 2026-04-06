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
@Table(name = "peer_review")
public class PeerReview {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "peer_review_id")
    private UUID peerReviewId;

    @Column(name = "submission_id")
    private UUID submissionId;

    @Column(name = "reviewer_user_id")
    private UUID reviewerUserId;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UUID getPeerReviewId() {
        return peerReviewId;
    }

    public void setPeerReviewId(UUID peerReviewId) {
        this.peerReviewId = peerReviewId;
    }

    public UUID getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(UUID submissionId) {
        this.submissionId = submissionId;
    }

    public UUID getReviewerUserId() {
        return reviewerUserId;
    }

    public void setReviewerUserId(UUID reviewerUserId) {
        this.reviewerUserId = reviewerUserId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
