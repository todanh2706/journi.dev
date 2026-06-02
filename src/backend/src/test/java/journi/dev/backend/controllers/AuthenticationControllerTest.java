package journi.dev.backend.controllers;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import journi.dev.backend.entities.User;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.dtos.responses.UserResponse;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.services.AuthenticationService;
import journi.dev.backend.services.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;

@WebMvcTest(controllers = AuthenticationController.class, excludeAutoConfiguration = {
                SecurityAutoConfiguration.class })
@AutoConfigureRestTestClient
public class AuthenticationControllerTest {
        @Autowired
        private RestTestClient restTestClient;

        @MockitoBean
        private JwtService jwtService;

        @MockitoBean
        private UserDetailsService userDetailsService;

        @MockitoBean
        private AuthenticationService authenticationService;

        @DisplayName("[TEST] Sign up with VALID UserRequest")
        @Test
        void signUpTestWithValidUserRequest() {
                // ==========================================
                // ARRANGE
                // ==========================================

                // Mock request
                UserRequest mockUserRequest = new UserRequest("test_user", "testuser@gmail.com", "testpassword");

                // Mock response
                UserResponse mockUserResponse = new UserResponse(
                                UUID.fromString("cf6ee0a3-a316-4503-94b9-07fe230fe07d"),
                                "test_user", "testuser@gmail.com", UserRole.USER, UserStatus.ACTIVE,
                                LocalDateTime.now(), null, null);

                // Mockito
                when(authenticationService.signup(any(UserRequest.class))).thenReturn(mockUserResponse);

                // ==========================================
                // ACT and ASSERT
                // ==========================================
                restTestClient.post()
                                .uri("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(mockUserRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody(UserResponse.class)
                                .value(response -> {
                                        assertEquals("test_user", response.getUsername());
                                        assertEquals("testuser@gmail.com", response.getEmail());
                                        assertEquals(UserRole.USER, response.getRole());
                                        assertEquals(UserStatus.ACTIVE, response.getStatus());
                                });

                // Mockito verification to ensure the controller actually called to signup
                // function in AuthenticationService
                verify(authenticationService, times(1)).signup(any(UserRequest.class));
        }

        @DisplayName("[TEST] Sign up with ALL invalid UserRequests")
        @ParameterizedTest(name = "Test {index}: User={0}, Email={1}, Password={2} -> Return error 400")
        @CsvSource({
                        // Empty username
                        ",testuser@gmail.com, strongpassword",
                        // Invalid email
                        "test_user, invalid_email, strongpassword",
                        // Invalid password
                        "test_user, testuser@gmail.com, 1"
        })
        void signUpTestWithInvalidRequest(String username, String email, String password) {
                // ==========================================
                // ARRANGE
                // ==========================================

                // Mock request
                UserRequest mockUserRequest = new UserRequest(username, email, password);

                // Mock response is not necessary for this case

                // ==========================================
                // ACT and ASSERT
                // ==========================================
                restTestClient.post()
                                .uri("/api/v1/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(mockUserRequest)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(400)
                                .jsonPath("$.error").isEqualTo("Bad Request")
                                .jsonPath("$.validationErrors").exists();
        }

        @DisplayName("[TEST] Sign in with valid request")
        @Test
        void signInTestWithValidRequest() {
                // ==========================================
                // ARRANGE
                // ==========================================

                // Mock request
                LoginUserRequest mockLoginRequest = new LoginUserRequest("test_user", "password");

                // Mock authenticated user
                User mockAuthenticatedUser = new User();
                mockAuthenticatedUser.setUsername("test_user");

                // Mock response
                // Mock authenticate function
                when(authenticationService.authenticate(any(LoginUserRequest.class))).thenReturn(mockAuthenticatedUser);

                // Mock generateToken function
                when(jwtService.generateToken(any(User.class))).thenReturn(
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30");

                // Mock getExpirationTime function
                when(jwtService.getExpirationTime()).thenReturn(3600000L);

                // ==========================================
                // ACT and ASSERT
                // ==========================================
                restTestClient.post()
                                .uri("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(mockLoginRequest)
                                .exchange()
                                .expectStatus().isOk()
                                .expectBody()
                                .jsonPath("$.token")
                                .isEqualTo("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30")
                                .jsonPath("$.expiresIn").isEqualTo(3600000L);
        }

        @DisplayName("[TEST] Sign in with ALL invalid requests")
        @ParameterizedTest(name = "Test {index}: User={0}, Password={1} -> Return error 400")
        @CsvSource({
                        // Empty username
                        ", password",

                        // Invalid password
                        "testuser, 1"
        })
        void signInTestWithInvalidRequest(String username, String password) {
                // ==========================================
                // ARRANGE
                // ==========================================

                // Mock request
                LoginUserRequest mockLoginUserRequest = new LoginUserRequest(username, password);

                // Mock response is not necessary for this case

                // ==========================================
                // ACT and ASSERT
                // ==========================================
                restTestClient.post()
                                .uri("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(mockLoginUserRequest)
                                .exchange()
                                .expectStatus().isBadRequest()
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(400)
                                .jsonPath("$.error").isEqualTo("Bad Request")
                                .jsonPath("$.validationErrors").exists();
        }

        @Disabled
        @DisplayName("[TEST] Sign in with bad credentials")
        @Test
        void signInTestWithBadCredentials() {
                // ==========================================
                // ARRANGE
                // ==========================================

                // Mock request
                LoginUserRequest mockBadLoginRequest = new LoginUserRequest("test_user_wrong", "password");

                // Mock authenticated user
                User mockAuthenticatedUser = new User();
                mockAuthenticatedUser.setUsername("test_user_right");

                // Mock response
                // Mock authenticate function
                when(authenticationService.authenticate(any(LoginUserRequest.class)))
                                .thenThrow(new BadCredentialsException("Invalid Username or Password"));

                // ==========================================
                // ACT and ASSERT
                // ==========================================
                restTestClient.post()
                                .uri("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(mockBadLoginRequest)
                                .exchange()
                                .expectStatus().isUnauthorized()
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(401)
                                .jsonPath("$.error").isEqualTo("Unauthorized")
                                .jsonPath("$.validationErrors").exists();
        }
}
