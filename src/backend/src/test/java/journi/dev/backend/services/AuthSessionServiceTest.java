package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.entities.User;

@ExtendWith(MockitoExtension.class)
class AuthSessionServiceTest {
    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshSessionService refreshSessionService;

    private AuthSessionService authSessionService;

    @BeforeEach
    void setUp() {
        authSessionService = new AuthSessionService(
                authenticationService, jwtService, refreshSessionService);
    }

    @Test
    void loginCombinesAccessResponseWithOneTimeRefreshTransportValue() {
        LoginUserRequest request = new LoginUserRequest("journi-user", "password");
        User user = new User();
        user.setUsername("journi-user");
        Instant refreshExpiry = Instant.parse("2026-07-21T00:00:00Z");
        when(authenticationService.authenticate(request)).thenReturn(user);
        when(refreshSessionService.issue(user))
                .thenReturn(new RefreshSessionToken("raw-refresh", refreshExpiry, user));
        when(jwtService.generateToken(user)).thenReturn("access-jwt");
        when(jwtService.getExpirationTime()).thenReturn(900_000L);

        AuthSessionResult result = authSessionService.login(request);

        assertThat(result.loginResponse().getToken()).isEqualTo("access-jwt");
        assertThat(result.loginResponse().getExpiresIn()).isEqualTo(900_000L);
        assertThat(result.refreshToken()).isEqualTo("raw-refresh");
        assertThat(result.refreshExpiresAt()).isEqualTo(refreshExpiry);
    }

    @Test
    void logoutDelegatesWithoutRequiringAccessAuthentication() {
        authSessionService.logout("refresh-token");

        verify(refreshSessionService).revokeForLogout("refresh-token");
    }
}
