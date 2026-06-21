package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import journi.dev.backend.configurations.AuthSessionProperties;

class RefreshCookieServiceTest {
    private static final Instant NOW = Instant.parse("2026-06-21T00:00:00Z");

    @Test
    void rotatedCookieMaxAgeUsesOnlyRemainingAbsoluteLifetime() {
        AuthSessionProperties properties = properties();
        RefreshCookieService service = new RefreshCookieService(
                properties, Clock.fixed(NOW, ZoneOffset.UTC));
        Instant familyExpiresAt = NOW.plus(Duration.ofDays(12)).plusSeconds(7);

        ResponseCookie cookie = service.create("rotated-refresh", familyExpiresAt);

        assertThat(cookie.getMaxAge()).isEqualTo(Duration.ofDays(12).plusSeconds(7));
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/api/v1/auth");
        assertThat(cookie.getSameSite()).isEqualTo("Lax");
    }

    @Test
    void sameSiteNoneRequiresSecureCookie() {
        AuthSessionProperties properties = properties();
        properties.getRefreshCookie().setSameSite("None");
        properties.getRefreshCookie().setSecure(false);

        assertThatThrownBy(() -> new RefreshCookieService(
                properties, Clock.fixed(NOW, ZoneOffset.UTC)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("requires a Secure refresh cookie");
    }

    private AuthSessionProperties properties() {
        AuthSessionProperties properties = new AuthSessionProperties();
        properties.getRefreshCookie().setName("journi_refresh");
        properties.getRefreshCookie().setPath("/api/v1/auth");
        properties.getRefreshCookie().setSameSite("Lax");
        return properties;
    }
}
