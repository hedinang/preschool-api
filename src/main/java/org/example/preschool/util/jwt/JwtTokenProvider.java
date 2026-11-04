package org.example.preschool.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.example.preschool.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Duration TOKEN_LIFETIME = Duration.ofDays(7);

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey signingKey;

    @PostConstruct
    void initialize() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException(
                    "JWT_SECRET is not configured. Set it to a Base64-encoded random key of at least 64 bytes."
            );
        }

        final byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(jwtSecret);
        } catch (IllegalArgumentException exception) {
            throw new IllegalStateException("JWT_SECRET must be valid Base64.", exception);
        }

        if (keyBytes.length < 64) {
            throw new IllegalStateException(
                    "JWT_SECRET must decode to at least 64 bytes for HS512."
            );
        }
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUserId())
                .claim("role", user.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(TOKEN_LIFETIME)))
                .signWith(signingKey)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
