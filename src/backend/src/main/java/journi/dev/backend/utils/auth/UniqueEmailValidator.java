package journi.dev.backend.utils.auth;

import org.springframework.stereotype.Component;
import journi.dev.backend.exceptions.BadRequestException;

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
            throw new BadRequestException("Email is already taken");
        }
    }
}
