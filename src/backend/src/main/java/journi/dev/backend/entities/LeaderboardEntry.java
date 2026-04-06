package journi.dev.backend.entities;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "leaderboard_entry")
@IdClass(LeaderboardEntryId.class)
public class LeaderboardEntry {
    @Id
    @Column(name = "leaderboard_id")
    private UUID leaderboardId;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
