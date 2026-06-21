package journi.dev.backend.exceptions;

public class RefreshSessionException extends RuntimeException {
    public RefreshSessionException() {
        super("Refresh session is invalid");
    }
}
