package journi.dev.backend.entities;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "refresh_sessions", indexes = {
        @Index(name = "idx_refresh_sessions_family_id", columnList = "family_id"),
        @Index(name = "idx_refresh_sessions_expires_at", columnList = "expires_at")
})
public class RefreshSession {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "refresh_session_id")
    private UUID refreshSessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "family_id", nullable = false, updatable = false)
    private UUID familyId;

    @Column(name = "token_hash", length = 64, nullable = false, unique = true, updatable = false)
    private String tokenHash;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_used_at")
    private Instant lastUsedAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "revocation_reason", length = 20)
    private RefreshSessionRevocationReason revocationReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by_session_id")
    private RefreshSession replacedBySession;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    public UUID getRefreshSessionId() {
        return refreshSessionId;
    }

    public void setRefreshSessionId(UUID refreshSessionId) {
        this.refreshSessionId = refreshSessionId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastUsedAt() {
        return lastUsedAt;
    }

    public void setLastUsedAt(Instant lastUsedAt) {
        this.lastUsedAt = lastUsedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public RefreshSessionRevocationReason getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(RefreshSessionRevocationReason revocationReason) {
        this.revocationReason = revocationReason;
    }

    public RefreshSession getReplacedBySession() {
        return replacedBySession;
    }

    public void setReplacedBySession(RefreshSession replacedBySession) {
        this.replacedBySession = replacedBySession;
    }

    public long getVersion() {
        return version;
    }

    public boolean isActiveAt(Instant instant) {
        return revokedAt == null && expiresAt.isAfter(instant);
    }
}
