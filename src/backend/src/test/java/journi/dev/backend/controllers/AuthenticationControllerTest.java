package journi.dev.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.dtos.responses.LoginResponse;
import journi.dev.backend.dtos.responses.UserResponse;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.exceptions.RefreshSessionException;
import journi.dev.backend.services.AuthSessionResult;
import journi.dev.backend.services.AuthSessionService;
import journi.dev.backend.services.AuthenticationService;
import journi.dev.backend.services.RefreshCookieService;
import journi.dev.backend.services.JwtService;

@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureRestTestClient
class AuthenticationControllerTest {
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "raw-refresh-token";
    private static final Instant REFRESH_EXPIRY = Instant.parse("2026-07-21T00:00:00Z");

    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private AuthSessionService authSessionService;

    @MockitoBean
    private RefreshCookieService refreshCookieService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void signupReturnsSanitizedUserResponse() {
        UserRequest request = new UserRequest("test_user", "testuser@gmail.com", "testpassword");
        UserResponse response = new UserResponse(
                UUID.randomUUID(),
                "test_user",
                "testuser@gmail.com",
                UserRole.USER,
                UserStatus.ACTIVE,
                LocalDateTime.now(),
                null,
                null);
        when(authenticationService.signup(any(UserRequest.class))).thenReturn(response);

        restTestClient.post()
                .uri("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("test_user")
                .jsonPath("$.passwordHash").doesNotExist();
    }

    @Test
    void invalidLoginRequestDoesNotCreateSession() {
        restTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginUserRequest("test_user", "1"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.validationErrors").exists();

        verify(authSessionService, never()).login(any(LoginUserRequest.class));
    }

    @Test
    void loginReturnsAccessPayloadAndHttpOnlyRefreshCookie() {
        LoginUserRequest request = new LoginUserRequest("test_user", "password");
        when(authSessionService.login(any(LoginUserRequest.class))).thenReturn(authResult());
        when(refreshCookieService.create(REFRESH_TOKEN, REFRESH_EXPIRY)).thenReturn(refreshCookie());

        restTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().value(HttpHeaders.SET_COOKIE, value -> {
                    assertThat(value).contains("journi_refresh=");
                    assertThat(value).contains("HttpOnly");
                })
                .expectBody()
                .jsonPath("$.token").isEqualTo(ACCESS_TOKEN)
                .jsonPath("$.expiresIn").isEqualTo(900_000);
    }

    @Test
    void badLoginCredentialsDoNotCreateCookie() {
        when(authSessionService.login(any(LoginUserRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid Username or Password"));

        restTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new LoginUserRequest("test_user", "wrong-password"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().doesNotExist(HttpHeaders.SET_COOKIE)
                .expectBody()
                .jsonPath("$.message").isEqualTo("Invalid username or password");
    }

    @Test
    void refreshRotatesCookieAndReturnsNewAccessPayload() {
        when(refreshCookieService.read(any())).thenReturn(REFRESH_TOKEN);
        when(authSessionService.refresh(REFRESH_TOKEN)).thenReturn(authResult());
        when(refreshCookieService.create(REFRESH_TOKEN, REFRESH_EXPIRY)).thenReturn(refreshCookie());

        restTestClient.post()
                .uri("/api/v1/auth/refresh")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().exists(HttpHeaders.SET_COOKIE)
                .expectBody()
                .jsonPath("$.token").isEqualTo(ACCESS_TOKEN);
    }

    @Test
    void invalidRefreshReturnsGenericUnauthorizedAndClearsCookie() {
        when(refreshCookieService.read(any())).thenReturn("invalid-refresh");
        when(authSessionService.refresh("invalid-refresh")).thenThrow(new RefreshSessionException());
        when(refreshCookieService.clear()).thenReturn(clearedCookie());

        restTestClient.post()
                .uri("/api/v1/auth/refresh")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().value(HttpHeaders.SET_COOKIE, value -> assertThat(value).contains("Max-Age=0"))
                .expectBody()
                .jsonPath("$.message").isEqualTo("Refresh session is invalid or expired");
    }

    @Test
    void logoutWithoutRefreshStateStillClearsCookieAndReturnsNoContent() {
        when(refreshCookieService.read(any())).thenReturn(null);
        when(refreshCookieService.clear()).thenReturn(clearedCookie());

        restTestClient.post()
                .uri("/api/v1/auth/logout")
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().value(HttpHeaders.SET_COOKIE, value -> assertThat(value).contains("Max-Age=0"))
                .expectBody().isEmpty();

        verify(authSessionService).logout(null);
    }

    private AuthSessionResult authResult() {
        return new AuthSessionResult(
                new LoginResponse(ACCESS_TOKEN, 900_000L),
                REFRESH_TOKEN,
                REFRESH_EXPIRY);
    }

    private ResponseCookie refreshCookie() {
        return ResponseCookie.from("journi_refresh", REFRESH_TOKEN)
                .httpOnly(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .maxAge(3600)
                .build();
    }

    private ResponseCookie clearedCookie() {
        return ResponseCookie.from("journi_refresh", "")
                .httpOnly(true)
                .path("/api/v1/auth")
                .sameSite("Lax")
                .maxAge(0)
                .build();
    }
}
