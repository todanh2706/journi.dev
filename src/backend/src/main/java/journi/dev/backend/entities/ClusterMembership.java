package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "cluster_membership")
@IdClass(ClusterMembershipId.class)
public class ClusterMembership {
    @Id
    @Column(name = "cluster_id")
    private UUID clusterId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

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

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
