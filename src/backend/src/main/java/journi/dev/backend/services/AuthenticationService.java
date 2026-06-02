package journi.dev.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import journi.dev.backend.exceptions.BadRequestException;
import journi.dev.backend.exceptions.ResourceNotFoundException;

import java.util.List;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.dtos.responses.UserResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.entities.UserRole;
import journi.dev.backend.entities.UserStatus;
import journi.dev.backend.mappers.UserMapper;
import journi.dev.backend.repositories.UserRepository;
import journi.dev.backend.utils.auth.SignupValidator;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final List<SignupValidator> validators;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            List<SignupValidator> validators) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.validators = validators;
    }

    @Transactional
    public UserResponse signup(UserRequest input) {
        for (SignupValidator validator : validators) {
            validator.validate(input);
        }

        User user = userMapper.toEntity(input);
        user.setPasswordHash(passwordEncoder.encode(input.getPassword()));
        user.setStatus(UserStatus.ACTIVE);
        user.setRole(UserRole.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public User authenticate(LoginUserRequest input) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()));

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid Username or Password"));
    }
}
