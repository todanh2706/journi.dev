package journi.dev.backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import journi.dev.backend.configurations.AuthEndpoints;
import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.dtos.responses.CsrfResponse;
import journi.dev.backend.dtos.responses.LoginResponse;
import journi.dev.backend.dtos.responses.UserResponse;
import journi.dev.backend.services.AuthSessionResult;
import journi.dev.backend.services.AuthSessionService;
import journi.dev.backend.services.AuthenticationService;
import journi.dev.backend.services.RefreshCookieService;

@RequestMapping(AuthEndpoints.BASE)
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthSessionService authSessionService;
    private final RefreshCookieService refreshCookieService;

    public AuthenticationController(
            AuthenticationService authenticationService,
            AuthSessionService authSessionService,
            RefreshCookieService refreshCookieService) {
        this.authenticationService = authenticationService;
        this.authSessionService = authSessionService;
        this.refreshCookieService = refreshCookieService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest registerUserDto) {
        UserResponse response = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginUserRequest loginUserRequest) {
        return authenticatedResponse(authSessionService.login(loginUserRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
        String refreshToken = refreshCookieService.read(request);
        return authenticatedResponse(authSessionService.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authSessionService.logout(refreshCookieService.read(request));
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, refreshCookieService.clear().toString())
                .build();
    }

    @GetMapping("/csrf")
    public CsrfResponse csrf(CsrfToken csrfToken) {
        return new CsrfResponse(csrfToken.getHeaderName(), csrfToken.getToken());
    }

    private ResponseEntity<LoginResponse> authenticatedResponse(AuthSessionResult result) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookieService
                        .create(result.refreshToken(), result.refreshExpiresAt()).toString())
                .body(result.loginResponse());
    }
}
