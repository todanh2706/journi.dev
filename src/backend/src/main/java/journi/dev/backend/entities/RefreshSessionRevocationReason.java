package journi.dev.backend.entities;

public enum RefreshSessionRevocationReason {
    ROTATED,
    LOGOUT,
    EXPIRED,
    REPLAY,
    ACCOUNT_DISABLED
}
