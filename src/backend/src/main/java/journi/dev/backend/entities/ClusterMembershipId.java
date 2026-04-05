package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ClusterMembershipId implements Serializable {
    private UUID clusterId;
    private UUID userId;

    public ClusterMembershipId() {
    };

    public ClusterMembershipId(UUID clusterId, UUID userId) {
        this.clusterId = clusterId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClusterMembershipId that = (ClusterMembershipId) o;
        return Objects.equals(clusterId, that.clusterId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, userId);
    }
}
