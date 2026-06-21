package journi.dev.backend.services;

import java.time.Instant;

import journi.dev.backend.dtos.responses.LoginResponse;

public record AuthSessionResult(LoginResponse loginResponse, String refreshToken, Instant refreshExpiresAt) {
}
