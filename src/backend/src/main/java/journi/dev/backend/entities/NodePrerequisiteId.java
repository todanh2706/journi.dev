package journi.dev.backend.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class NodePrerequisiteId implements Serializable {
    private UUID parentNode;
    private UUID childNode;

    public NodePrerequisiteId() {
    }

    public NodePrerequisiteId(UUID parentNodeId, UUID childNodeId) {
        this.parentNode = parentNodeId;
        this.childNode = childNodeId;
    }

    public UUID getParentNode() {
        return parentNode;
    }

    public void setParentNode(UUID parentNode) {
        this.parentNode = parentNode;
    }

    public UUID getChildNode() {
        return childNode;
    }

    public void setChildNode(UUID childNode) {
        this.childNode = childNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        NodePrerequisiteId that = (NodePrerequisiteId) o;
        return Objects.equals(parentNode, that.parentNode) && Objects.equals(childNode, that.childNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentNode, childNode);
    }
}
