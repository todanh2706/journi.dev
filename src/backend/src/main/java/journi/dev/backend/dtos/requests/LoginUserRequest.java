package journi.dev.backend.dtos.requests;

import jakarta.validation.constraints.NotBlank;

public class LoginUserRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public LoginUserRequest() {
    }

    public LoginUserRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
