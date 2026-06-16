package journi.dev.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import journi.dev.backend.entities.NodePrerequisite;
import journi.dev.backend.entities.NodePrerequisiteId;

public interface NodePrerequisiteRepository extends JpaRepository<NodePrerequisite, NodePrerequisiteId> {
    List<NodePrerequisite> findByChildNode_NodeId(UUID childNodeId);

    List<NodePrerequisite> findByChildNode_NodeIdIn(Collection<UUID> childNodeIds);
}
