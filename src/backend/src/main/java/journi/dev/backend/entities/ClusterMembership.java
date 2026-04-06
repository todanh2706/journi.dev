package journi.dev.backend.entities;

import java.time.LocalDateTime;
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
@Table(name = "cluster_membership")
@IdClass(ClusterMembershipId.class)
public class ClusterMembership {
    @Id
    @Column(name = "cluster_id")
    private UUID clusterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User joinee;

    @Column(name = "member_role", length = 30)
    private String memberRole;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    public UUID getClusterId() {
        return clusterId;
    }

    public void setClusterId(UUID clusterId) {
        this.clusterId = clusterId;
    }

    public User getJoinee() {
        return joinee;
    }

    public void setJoinee(User joinee) {
        this.joinee = joinee;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

}
