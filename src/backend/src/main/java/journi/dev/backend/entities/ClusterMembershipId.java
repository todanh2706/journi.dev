package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ClusterMembershipId implements Serializable {
    private UUID clusterId;
    private UUID joinee;

    public ClusterMembershipId() {
    };

    public ClusterMembershipId(UUID clusterId, UUID joinee) {
        this.clusterId = clusterId;
        this.joinee = joinee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ClusterMembershipId that = (ClusterMembershipId) o;
        return Objects.equals(clusterId, that.clusterId) && Objects.equals(joinee, that.joinee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, joinee);
    }
}
