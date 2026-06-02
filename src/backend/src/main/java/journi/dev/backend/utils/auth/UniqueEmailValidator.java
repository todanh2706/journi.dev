package journi.dev.backend.utils.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import journi.dev.backend.dtos.requests.UserRequest;
import journi.dev.backend.repositories.UserRepository;

@Component
public class UniqueEmailValidator implements SignupValidator {
    private final UserRepository userRepository;

    public UniqueEmailValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(UserRequest input) {
        if (userRepository.existsByEmail(input.getEmail()) == true) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already taken");
        }
    }
}
