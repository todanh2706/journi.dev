package journi.dev.backend.utils.auth;

import journi.dev.backend.exceptions.BadRequestException;

import org.springframework.stereotype.Component;

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
            throw new BadRequestException("Username is already taken");
        }
    }
}
