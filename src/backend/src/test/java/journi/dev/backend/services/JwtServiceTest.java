package journi.dev.backend.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import journi.dev.backend.configurations.AuthSessionProperties;

class JwtServiceTest {
    private static final String SECRET = "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        AuthSessionProperties properties = new AuthSessionProperties();
        properties.setAccessTokenLifetime(Duration.ofMinutes(15));
        jwtService = new JwtService(SECRET, properties);
        userDetails = User.withUsername("journi-user").password("password").authorities("USER").build();
    }

    @Test
    void generatedTokenIsTypedAsAccessAndUsesConfiguredLifetime() {
        String token = jwtService.generateToken(userDetails);
        String tokenType = jwtService.extractClaim(token, claims -> claims.get("token_type", String.class));

        assertThat(tokenType).isEqualTo("access");
        assertThat(jwtService.getExpirationTime()).isEqualTo(Duration.ofMinutes(15).toMillis());
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    void tokenWithNonAccessTypeIsRejected() {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        String refreshLikeJwt = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("token_type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        assertThat(jwtService.isTokenValid(refreshLikeJwt, userDetails)).isFalse();
    }
}
