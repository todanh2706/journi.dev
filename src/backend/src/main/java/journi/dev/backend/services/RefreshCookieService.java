package journi.dev.backend.services;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import journi.dev.backend.configurations.AuthSessionProperties;

@Service
public class RefreshCookieService {
    private static final Set<String> ALLOWED_SAME_SITE = Set.of("Lax", "Strict", "None");

    private final AuthSessionProperties.RefreshCookie properties;
    private final Clock clock;

    public RefreshCookieService(AuthSessionProperties authSessionProperties, Clock clock) {
        this.properties = authSessionProperties.getRefreshCookie();
        this.clock = clock;
        validateConfiguration();
    }

    public ResponseCookie create(String refreshToken, Instant absoluteExpiresAt) {
        long remainingSeconds = Math.max(0, Duration.between(clock.instant(), absoluteExpiresAt).getSeconds());
        return cookie(refreshToken)
                .maxAge(Duration.ofSeconds(remainingSeconds))
                .build();
    }

    public ResponseCookie clear() {
        return cookie("")
                .maxAge(Duration.ZERO)
                .build();
    }

    public String read(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
                .filter(cookie -> properties.getName().equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    private ResponseCookie.ResponseCookieBuilder cookie(String value) {
        return ResponseCookie.from(properties.getName(), value)
                .httpOnly(true)
                .secure(properties.isSecure())
                .sameSite(normalizeSameSite(properties.getSameSite()))
                .path(properties.getPath());
    }

    private void validateConfiguration() {
        if (!StringUtils.hasText(properties.getName()) || !StringUtils.hasText(properties.getPath())) {
            throw new IllegalStateException("Refresh cookie name and path are required");
        }
        String sameSite = normalizeSameSite(properties.getSameSite());
        if (!ALLOWED_SAME_SITE.contains(sameSite)) {
            throw new IllegalStateException("Refresh cookie SameSite must be Lax, Strict, or None");
        }
        if ("None".equals(sameSite) && !properties.isSecure()) {
            throw new IllegalStateException("SameSite=None requires a Secure refresh cookie");
        }
    }

    private String normalizeSameSite(String sameSite) {
        if (!StringUtils.hasText(sameSite)) {
            return "Lax";
        }
        String lowerCase = sameSite.toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lowerCase.charAt(0)) + lowerCase.substring(1);
    }
}
