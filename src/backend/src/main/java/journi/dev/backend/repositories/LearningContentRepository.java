package journi.dev.backend.repositories;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import journi.dev.backend.entities.LearningContent;

public interface LearningContentRepository extends JpaRepository<LearningContent, UUID> {
    List<LearningContent> findByNode_NodeId(UUID nodeId);

    List<LearningContent> findByNode_NodeIdIn(Collection<UUID> nodeIds);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            delete from LearningContent learningContent
            where learningContent.node.nodeId in :nodeIds
            """)
    void deleteByNodeIds(@Param("nodeIds") Collection<UUID> nodeIds);
}
