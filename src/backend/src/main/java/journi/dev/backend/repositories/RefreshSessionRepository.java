package journi.dev.backend.repositories;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import journi.dev.backend.entities.RefreshSession;
import journi.dev.backend.entities.RefreshSessionRevocationReason;

public interface RefreshSessionRepository extends JpaRepository<RefreshSession, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select session from RefreshSession session where session.tokenHash = :tokenHash")
    Optional<RefreshSession> findByTokenHashForUpdate(@Param("tokenHash") String tokenHash);

    Optional<RefreshSession> findByTokenHash(String tokenHash);

    List<RefreshSession> findAllByFamilyId(UUID familyId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update RefreshSession session
               set session.revokedAt = :revokedAt,
                   session.revocationReason = :reason
             where session.familyId = :familyId
               and session.revokedAt is null
            """)
    int revokeActiveFamily(
            @Param("familyId") UUID familyId,
            @Param("revokedAt") Instant revokedAt,
            @Param("reason") RefreshSessionRevocationReason reason);

    @Query("""
            select distinct session.familyId
              from RefreshSession session
             where session.expiresAt < :cutoff
            """)
    List<UUID> findExpiredFamilyIds(@Param("cutoff") Instant cutoff, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RefreshSession session where session.familyId in :familyIds")
    int deleteFamilies(@Param("familyIds") List<UUID> familyIds);
}
