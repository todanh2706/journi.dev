package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.dtos.responses.UserResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.mappers.UserMapper;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.utils.auth.SignupValidator;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SignupValidator signupValidator;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        // Create service under test with all dependencies mocked by Mockito
        authenticationService = new AuthenticationService(
                userRepository,
                authenticationManager,
                passwordEncoder,
                userMapper,
                List.of(signupValidator));
    }

    @DisplayName("[TEST] Signup validates request, stores encoded password, and returns mapped response")
    @Test
    void signupCreatesActiveUserWithEncodedPassword() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        UserRequest request = new UserRequest("test_user", "testuser@gmail.com", "testpassword");

        // Mock entity that UserMapper creates from request
        User mappedUser = new User();
        mappedUser.setUsername(request.getUsername());
        mappedUser.setEmail(request.getEmail());

        // Mock saved entity that UserRepository returns after saving
        User savedUser = new User();
        savedUser.setUserId(UUID.fromString("cf6ee0a3-a316-4503-94b9-07fe230fe07d"));
        savedUser.setUsername(request.getUsername());
        savedUser.setEmail(request.getEmail());
        savedUser.setPasswordHash("encoded-password");
        savedUser.setRole(UserRole.USER);
        savedUser.setStatus(UserStatus.ACTIVE);
        savedUser.setEnabled(true);

        // Mock response that UserMapper creates from saved entity
        UserResponse expectedResponse = new UserResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getStatus(),
                LocalDateTime.now(),
                null,
                null);

        // Mockito
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(expectedResponse);

        // ==========================================
        // ACT
        // ==========================================
        UserResponse actualResponse = authenticationService.signup(request);

        // ==========================================
        // ASSERT
        // ==========================================
        assertThat(actualResponse).isSameAs(expectedResponse);

        // Mockito verification to ensure signup validation and password encoding are called
        verify(signupValidator).validate(request);
        verify(passwordEncoder).encode("testpassword");

        // Mockito verification to inspect the actual User entity sent to repository
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User userToSave = userCaptor.getValue();
        assertThat(userToSave.getPasswordHash()).isEqualTo("encoded-password");
        assertThat(userToSave.getRole()).isEqualTo(UserRole.USER);
        assertThat(userToSave.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(userToSave.getEnabled()).isTrue();
    }

    @DisplayName("[TEST] Signup stops when validator rejects request")
    @Test
    void signupStopsWhenValidatorRejectsRequest() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        UserRequest request = new UserRequest("bad_user", "baduser@gmail.com", "password");

        // Mockito
        doThrow(new BadRequestException("Signup request is invalid"))
                .when(signupValidator).validate(request);

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThatThrownBy(() -> authenticationService.signup(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Signup request is invalid");

        // Mockito verification to ensure validator is called first
        verify(signupValidator).validate(request);

        // Mockito verification to ensure service stops before creating or saving User
        verify(userMapper, never()).toEntity(any(UserRequest.class));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @DisplayName("[TEST] Signup stops when username is already taken")
    @Test
    void signupStopsWhenUsernameAlreadyExists() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        UserRequest request = new UserRequest("existing_user", "newemail@gmail.com", "password");

        // Mockito
        doThrow(new BadRequestException("Username is already taken"))
                .when(signupValidator).validate(request);

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThatThrownBy(() -> authenticationService.signup(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Username is already taken");

        // Mockito verification to ensure duplicate username blocks signup
        verify(signupValidator).validate(request);

        // Mockito verification to ensure duplicate username is not saved
        verify(userMapper, never()).toEntity(any(UserRequest.class));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @DisplayName("[TEST] Signup stops when email is already taken")
    @Test
    void signupStopsWhenEmailAlreadyExists() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        UserRequest request = new UserRequest("new_user", "existing@gmail.com", "password");

        // Mockito
        doThrow(new BadRequestException("Email is already taken"))
                .when(signupValidator).validate(request);

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThatThrownBy(() -> authenticationService.signup(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Email is already taken");

        // Mockito verification to ensure duplicate email blocks signup
        verify(signupValidator).validate(request);

        // Mockito verification to ensure duplicate email is not saved
        verify(userMapper, never()).toEntity(any(UserRequest.class));
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @DisplayName("[TEST] Authenticate delegates credentials and returns stored user")
    @Test
    void authenticateReturnsUserWhenCredentialsAreValid() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        LoginUserRequest request = new LoginUserRequest("test_user", "password");

        // Mock user returned by UserRepository after AuthenticationManager accepts credentials
        User user = new User();
        user.setUsername(request.getUsername());

        // Mockito
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

        // ==========================================
        // ACT
        // ==========================================
        User actualUser = authenticationService.authenticate(request);

        // ==========================================
        // ASSERT
        // ==========================================
        assertThat(actualUser).isSameAs(user);

        // Mockito verification to ensure credentials are delegated to AuthenticationManager
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Mockito verification to ensure service loads user by username after authentication
        verify(userRepository).findByUsername("test_user");

        // Mockito verification to ensure authentication happens before repository lookup
        InOrder inOrder = inOrder(authenticationManager, userRepository);
        inOrder.verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        inOrder.verify(userRepository).findByUsername(request.getUsername());
    }

    @DisplayName("[TEST] Authenticate throws BadCredentialsException when user cannot be loaded")
    @Test
    void authenticateThrowsBadCredentialsWhenUserDoesNotExist() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        LoginUserRequest request = new LoginUserRequest("missing_user", "password");

        // Mockito
        when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Username or Password");

        // Mockito verification to ensure credentials are still checked first
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // Mockito verification to ensure repository lookup only happens after credentials pass
        InOrder inOrder = inOrder(authenticationManager, userRepository);
        inOrder.verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        inOrder.verify(userRepository).findByUsername(request.getUsername());
    }

    @DisplayName("[TEST] Authenticate does not query user repository when credentials are rejected")
    @Test
    void authenticateStopsWhenAuthenticationManagerRejectsCredentials() {
        // ==========================================
        // ARRANGE
        // ==========================================

        // Mock request
        LoginUserRequest request = new LoginUserRequest("test_user", "wrongpassword");

        // Mock authentication token that should be sent to AuthenticationManager
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        // Mockito
        when(authenticationManager.authenticate(token))
                .thenThrow(new BadCredentialsException("Invalid Username or Password"));

        // ==========================================
        // ACT and ASSERT
        // ==========================================
        assertThatThrownBy(() -> authenticationService.authenticate(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid Username or Password");

        // Mockito verification to ensure repository is not called when credentials fail
        verify(userRepository, never()).findByUsername(any());
    }
}
