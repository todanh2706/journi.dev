package journi.dev.backend.services;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import journi.dev.backend.configurations.AuthSessionProperties;

@Service
public class JwtService {
    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private final String secretKey;
    private final AuthSessionProperties properties;

    public JwtService(
            @Value("${security.jwt.secret-key}") String secretKey,
            AuthSessionProperties properties) {
        this.secretKey = secretKey;
        this.properties = properties;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        Map<String, Object> accessClaims = new HashMap<>(extraClaims);
        accessClaims.put(TOKEN_TYPE_CLAIM, ACCESS_TOKEN_TYPE);
        return buildToken(accessClaims, userDetails, getExpirationTime());
    }

    public long getExpirationTime() {
        return properties.getAccessTokenLifetime().toMillis();
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);
        return username.equals(userDetails.getUsername())
                && ACCESS_TOKEN_TYPE.equals(tokenType)
                && !claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
