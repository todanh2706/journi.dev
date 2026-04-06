package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class NodePrerequisiteId implements Serializable {
    private UUID parentNodeId;
    private UUID childNodeId;

    public NodePrerequisiteId() {
    }

    public NodePrerequisiteId(UUID parentNodeId, UUID childNodeId) {
        this.parentNodeId = parentNodeId;
        this.childNodeId = childNodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NodePrerequisiteId that = (NodePrerequisiteId) o;
        return Objects.equals(parentNodeId, that.parentNodeId) && Objects.equals(childNodeId, that.childNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentNodeId, childNodeId);
    }
}
