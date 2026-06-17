package journi.dev.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodePrerequisiteId;

public interface NodePrerequisiteRepository extends JpaRepository<NodePrerequisite, NodePrerequisiteId> {
    List<NodePrerequisite> findByChildNode_NodeId(UUID childNodeId);

    List<NodePrerequisite> findByChildNode_NodeIdIn(Collection<UUID> childNodeIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            delete from NodePrerequisite prerequisite
            where prerequisite.childNode.nodeId in :nodeIds
               or prerequisite.parentNode.nodeId in :nodeIds
            """)
    void deleteByNodeIds(@Param("nodeIds") Collection<UUID> nodeIds);
}
