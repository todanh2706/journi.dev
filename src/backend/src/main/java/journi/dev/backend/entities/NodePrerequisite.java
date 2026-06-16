package journi.dev.backend.entities;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "node_prerequisite", uniqueConstraints = {
        @UniqueConstraint(name = "uk_node_prerequisite_edge", columnNames = {
                "parent_node_id", "child_node_id"
        })
})
@IdClass(NodePrerequisiteId.class)
public class NodePrerequisite {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_node_id", nullable = false)
    private SkillNode parentNode;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_node_id", nullable = false)
    private SkillNode childNode;

    @Column(name = "relation_type", length = 50)
    private String relationType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    public SkillNode getParentNode() {
        return parentNode;
    }

    public void setParentNode(SkillNode parentNode) {
        this.parentNode = parentNode;
    }

    public SkillNode getChildNode() {
        return childNode;
    }

    public void setChildNode(SkillNode childNode) {
        this.childNode = childNode;
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
