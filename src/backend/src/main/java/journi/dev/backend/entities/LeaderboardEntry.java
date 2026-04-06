package journi.dev.backend.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "leaderboard_entry")
@IdClass(LeaderboardEntryId.class)
public class LeaderboardEntry {
    @Id
    @Column(name = "leaderboard_id")
    private UUID leaderboardId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "nodes_passed")
    private Integer nodesPassed;

    @Column(name = "revision_requests_count")
    private Integer revisionRequestsCount;

    @Column(name = "peer_review_contributions")
    private Integer peerReviewContributions;

    private Integer score;

    @Column(name = "rank_position")
    private Integer rankPosition;

    public UUID getLeaderboardId() {
        return leaderboardId;
    }

    public void setLeaderboardId(UUID leaderboardId) {
        this.leaderboardId = leaderboardId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public Integer getNodesPassed() {
        return nodesPassed;
    }

    public void setNodesPassed(Integer nodesPassed) {
        this.nodesPassed = nodesPassed;
    }

    public Integer getRevisionRequestsCount() {
        return revisionRequestsCount;
    }

    public void setRevisionRequestsCount(Integer revisionRequestsCount) {
        this.revisionRequestsCount = revisionRequestsCount;
    }

    public Integer getPeerReviewContributions() {
        return peerReviewContributions;
    }

    public void setPeerReviewContributions(Integer peerReviewContributions) {
        this.peerReviewContributions = peerReviewContributions;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
    }
}
