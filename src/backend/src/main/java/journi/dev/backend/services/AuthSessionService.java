package journi.dev.backend.services;

import org.springframework.stereotype.Service;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.responses.LoginResponse;
import journi.dev.backend.entities.User;

@Service
public class AuthSessionService {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;
    private final RefreshSessionService refreshSessionService;

    public AuthSessionService(
            AuthenticationService authenticationService,
            JwtService jwtService,
            RefreshSessionService refreshSessionService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
        this.refreshSessionService = refreshSessionService;
    }

    public AuthSessionResult login(LoginUserRequest request) {
        User user = authenticationService.authenticate(request);
        return createResult(user, refreshSessionService.issue(user));
    }

    public AuthSessionResult refresh(String refreshToken) {
        RefreshSessionToken rotated = refreshSessionService.rotate(refreshToken);
        return createResult(rotated.user(), rotated);
    }

    public void logout(String refreshToken) {
        refreshSessionService.revokeForLogout(refreshToken);
    }

    private AuthSessionResult createResult(User user, RefreshSessionToken refreshSession) {
        String accessToken = jwtService.generateToken(user);
        LoginResponse response = new LoginResponse(accessToken, jwtService.getExpirationTime());
        return new AuthSessionResult(response, refreshSession.value(), refreshSession.expiresAt());
    }
}
