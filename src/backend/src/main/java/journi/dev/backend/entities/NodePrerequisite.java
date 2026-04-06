package journi.dev.backend.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "node_prerequisite")
@IdClass(NodePrerequisiteId.class)
public class NodePrerequisite {
    @Id
    @Column(name = "parent_node_id")
    private UUID parentNodeId;

    @Id
    @Column(name = "child_node_id")
    private UUID childNodeId;

    @Column(name = "relation_type", length = 50)
    private String relationType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UUID getParentNodeId() {
        return parentNodeId;
    }

    public void setParentNodeId(UUID parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
