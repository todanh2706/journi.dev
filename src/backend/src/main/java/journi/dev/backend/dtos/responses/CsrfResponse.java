package journi.dev.backend.dtos.responses;

public class CsrfResponse {
    private final String headerName;
    private final String token;

    public CsrfResponse(String headerName, String token) {
        this.headerName = headerName;
        this.token = token;
    }

    public String getHeaderName() {
        return headerName;
    }

    public String getToken() {
        return token;
    }
}
