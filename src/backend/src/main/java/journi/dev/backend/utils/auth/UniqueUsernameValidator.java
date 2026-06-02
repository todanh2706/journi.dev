package journi.dev.backend.utils.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.repositories.UserRepository;

@Component
public class UniqueUsernameValidator implements SignupValidator {
    private final UserRepository userRepository;

    public UniqueUsernameValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(UserRequest input) {
        if (userRepository.existsByUsername(input.getUsername())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken");
        }
    }
}
