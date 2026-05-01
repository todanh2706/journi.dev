package journi.dev.backend.dtos.requests;

import java.util.UUID;

public class NodePrerequisiteRequest {
    private UUID childNodeId;
    private String relationType;

    public UUID getChildNodeId() {
        return childNodeId;
    }

    public void setChildNodeId(UUID childNodeId) {
        this.childNodeId = childNodeId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public NodePrerequisiteRequest(UUID childNodeId, String relationType) {
        this.childNodeId = childNodeId;
        this.relationType = relationType;
    }
}
