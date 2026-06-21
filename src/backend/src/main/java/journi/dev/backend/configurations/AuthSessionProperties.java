package journi.dev.backend.configurations;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

// This class will bind configs to Java object
@Component
@Validated
@ConfigurationProperties(prefix = "security.auth")
public class AuthSessionProperties {
    private Duration accessTokenLifetime = Duration.ofMinutes(15);
    private Duration refreshTokenLifetime = Duration.ofDays(30);
    @NotEmpty(message = "FRONTEND_ALLOWED_ORIGINS must contain at least one exact frontend origin")
    private List<@NotBlank String> allowedOrigins = new ArrayList<>();
    private final RefreshCookie refreshCookie = new RefreshCookie();
    private final Csrf csrf = new Csrf();

    public Duration getAccessTokenLifetime() {
        return accessTokenLifetime;
    }

    public void setAccessTokenLifetime(Duration accessTokenLifetime) {
        this.accessTokenLifetime = accessTokenLifetime;
    }

    public Duration getRefreshTokenLifetime() {
        return refreshTokenLifetime;
    }

    public void setRefreshTokenLifetime(Duration refreshTokenLifetime) {
        this.refreshTokenLifetime = refreshTokenLifetime;
    }

    public List<String> getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(List<String> allowedOrigins) {
        this.allowedOrigins = new ArrayList<>(allowedOrigins);
    }

    public RefreshCookie getRefreshCookie() {
        return refreshCookie;
    }

    public Csrf getCsrf() {
        return csrf;
    }

    public static class RefreshCookie {
        private String name = "journi_refresh";
        private String path = "/api/v1/auth";
        private boolean secure;
        private String sameSite = "Lax";

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isSecure() {
            return secure;
        }

        public void setSecure(boolean secure) {
            this.secure = secure;
        }

        public String getSameSite() {
            return sameSite;
        }

        public void setSameSite(String sameSite) {
            this.sameSite = sameSite;
        }
    }

    public static class Csrf {
        private String cookieName = "journi_csrf";
        private String headerName = "X-CSRF-TOKEN";

        public String getCookieName() {
            return cookieName;
        }

        public void setCookieName(String cookieName) {
            this.cookieName = cookieName;
        }

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }
}
