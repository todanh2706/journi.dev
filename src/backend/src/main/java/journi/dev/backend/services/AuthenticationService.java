package journi.dev.backend.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import journi.dev.backend.dtos.requests.LoginUserRequest;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.UserRepository;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(UserRequest input) {
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already registered");
        }

        User user = new User();
        user.setUsername(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPasswordHash(passwordEncoder.encode(input.getPassword()));
        user.setStatus("ACTIVE");
        user.setRole("USER");
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public User authenticate(LoginUserRequest input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getUsername(),
                        input.getPassword()));

        return userRepository.findByUsername(input.getUsername())
                .orElseThrow();
    }
}
