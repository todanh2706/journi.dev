package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ClusterNodeMappingId implements Serializable {
    private UUID clusterId;
    private UUID nodeId;

    public ClusterNodeMappingId() {
    }

    public ClusterNodeMappingId(UUID clusterId, UUID nodeId) {
        this.clusterId = clusterId;
        this.nodeId = nodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ClusterNodeMappingId that = (ClusterNodeMappingId) o;
        return Objects.equals(clusterId, that.clusterId) && Objects.equals(nodeId, that.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clusterId, nodeId);
    }
}
