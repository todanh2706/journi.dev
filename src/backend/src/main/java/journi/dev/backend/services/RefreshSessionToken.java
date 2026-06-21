package journi.dev.backend.services;

import java.time.Instant;

import journi.dev.backend.entities.User;

public record RefreshSessionToken(String value, Instant expiresAt, User user) {
}
