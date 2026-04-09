package journi.dev.backend.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.dtos.responses.UserResponse;
import journi.dev.backend.entities.User;
import journi.dev.backend.repositories.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(user -> new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreateAt(),
                user.getUpdatedAt(),
                user.getDeletedAt())).collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id).orElse(null);
        return new UserResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                user.getCreateAt(),
                user.getUpdatedAt(),
                user.getDeletedAt());
    }

    public UserResponse createUser(UserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setStatus("ACTIVE");
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getStatus(),
                savedUser.getCreateAt(),
                savedUser.getUpdatedAt(),
                savedUser.getDeletedAt());
    }

    public UserResponse updateUser(UUID id, User user) {
        if (userRepository.existsById(id)) {
            user.setUserId(id);
            User updatedUser = userRepository.save(user);
            return new UserResponse(
                    updatedUser.getUserId(),
                    updatedUser.getUsername(),
                    updatedUser.getEmail(),
                    updatedUser.getRole(),
                    updatedUser.getStatus(),
                    updatedUser.getCreateAt(),
                    updatedUser.getUpdatedAt(),
                    updatedUser.getDeletedAt());
        }
        return null;
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
