package journi.dev.backend.utils.auth;

import journi.dev.backend.dtos.requests.UserRequest;

public interface SignupValidator {
    void validate(UserRequest input);
}
