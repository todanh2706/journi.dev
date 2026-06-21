package journi.dev.backend.configurations;

import java.util.Set;

public final class AuthEndpoints {
    public static final String BASE = "/api/v1/auth";
    public static final String SIGNUP = BASE + "/signup";
    public static final String LOGIN = BASE + "/login";
    public static final String REFRESH = BASE + "/refresh";
    public static final String LOGOUT = BASE + "/logout";
    public static final String CSRF = BASE + "/csrf";
    public static final Set<String> CSRF_PROTECTED = Set.of(LOGIN, REFRESH, LOGOUT);

    private AuthEndpoints() {
    }
}
