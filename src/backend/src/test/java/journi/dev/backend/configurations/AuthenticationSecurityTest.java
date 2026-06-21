package journi.dev.backend.configurations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import journi.dev.backend.controllers.AuthenticationController;
import journi.dev.backend.dtos.responses.CsrfResponse;
import journi.dev.backend.services.AuthSessionService;
import journi.dev.backend.services.AuthenticationService;
import journi.dev.backend.services.JwtService;
import journi.dev.backend.services.RefreshCookieService;

@WebMvcTest(controllers = AuthenticationController.class)
@Import({SecurityConfig.class, JwtFilter.class, AuthSessionProperties.class})
@AutoConfigureRestTestClient
class AuthenticationSecurityTest {
    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

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
    void cookieBackedAuthMutationWithoutCsrfIsForbidden() {
        restTestClient.post()
                .uri(AuthEndpoints.LOGOUT)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void csrfBootstrapReturnsFrontendReadableHeaderMaterial() {
        CsrfExchange csrf = csrf();

        assertThat(csrf.response().getHeaderName()).isEqualTo("X-CSRF-TOKEN");
        assertThat(csrf.response().getToken()).isNotBlank();
        assertThat(csrf.cookieValue()).isNotBlank();
    }

    @Test
    void logoutIsPublicAndIdempotentAfterValidCsrf() {
        CsrfExchange csrf = csrf();
        when(refreshCookieService.clear()).thenReturn(ResponseCookie.from("journi_refresh", "")
                .httpOnly(true)
                .path("/api/v1/auth")
                .maxAge(0)
                .build());

        restTestClient.post()
                .uri(AuthEndpoints.LOGOUT)
                .cookie("journi_csrf", csrf.cookieValue())
                .header(csrf.response().getHeaderName(), csrf.response().getToken())
                .exchange()
                .expectStatus().isNoContent()
                .expectHeader().value(HttpHeaders.SET_COOKIE, value -> assertThat(value).contains("Max-Age=0"));

        verify(authSessionService).logout(null);
    }

    @Test
    void credentialedCorsAllowsConfiguredFrontendOrigin() {
        restTestClient.options()
                .uri(AuthEndpoints.REFRESH)
                .header(HttpHeaders.ORIGIN, "http://localhost:3000")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "X-CSRF-TOKEN")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000")
                .expectHeader().valueEquals(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    }

    @Test
    void credentialedCorsRejectsUntrustedOrigin() {
        restTestClient.options()
                .uri(AuthEndpoints.REFRESH)
                .header(HttpHeaders.ORIGIN, "https://attacker.example")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .exchange()
                .expectStatus().isForbidden()
                .expectHeader().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
    }

    private CsrfExchange csrf() {
        var result = restTestClient.get()
                .uri(AuthEndpoints.CSRF)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CsrfResponse.class)
                .returnResult();
        CsrfResponse response = result.getResponseBody();
        String cookieValue = result.getResponseCookies().getFirst("journi_csrf").getValue();
        return new CsrfExchange(response, cookieValue);
    }

    private record CsrfExchange(CsrfResponse response, String cookieValue) {
    }
}
