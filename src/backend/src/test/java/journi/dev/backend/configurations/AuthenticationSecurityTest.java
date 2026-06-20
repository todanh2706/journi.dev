package journi.dev.backend.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import journi.dev.backend.controllers.AuthenticationController;
import journi.dev.backend.services.AuthenticationService;
import journi.dev.backend.services.JwtService;

@WebMvcTest(controllers = AuthenticationController.class)
@Import({SecurityConfig.class, JwtFilter.class})
@AutoConfigureRestTestClient
class AuthenticationSecurityTest {
    @Autowired
    private RestTestClient restTestClient;

    @MockitoBean
    private AuthenticationProvider authenticationProvider;

    @MockitoBean
    private AuthenticationService authenticationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void logoutWithoutAuthenticationIsRejected() {
        restTestClient.post()
                .uri("/api/v1/auth/logout")
                .exchange()
                .expectStatus().isUnauthorized();
    }

}
