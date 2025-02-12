package com.quickweather.security;
import com.quickweather.domain.User;
import com.quickweather.service.user.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey;
    private final SecretKey resetKey;
    private final long expirationTime;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.reset-secret}") String resetSecret,
                   @Value("${jwt.expiration}") long expirationTime) {
        this.expirationTime = expirationTime;

        // Dekodowanie kluczy z Base64
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.resetKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(resetSecret));
    }

    public Map<String, Object> generateToken(CustomUserDetails userDetails, UUID uuid) {
        String token = Jwts.builder()
                .setHeaderParam("type", "JWT")
                .setSubject(userDetails.getEmail())
                .claim("userId", userDetails.getUserId())
                .claim("name", userDetails.getName())
                .claim("email", userDetails.getEmail())
                .claim("uuid", uuid.toString())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        log.info("User authorities: {}", userDetails.getAuthorities());

        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("token", token);
        tokenResponse.put("expiresAt", new Date(System.currentTimeMillis() + expirationTime));
        tokenResponse.put("email", userDetails.getEmail());

        return tokenResponse;
    }

    public String generateResetToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("type", "reset-password")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 min ważności
                .signWith(resetKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token, secretKey);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateResetToken(String token) {
        try {
            Claims claims = parseClaims(token, resetKey);
            String type = claims.get("type", String.class);
            Date expiration = claims.getExpiration();

            log.info("Extracted type: {}", type);
            log.info("Token expiration time: {}", expiration);

            return "reset-password".equals(type) && expiration.after(new Date());
        } catch (JwtException e) {
            log.error("Invalid reset token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        return parseClaims(token, secretKey).getSubject();
    }

    public String extractUsernameFromResetToken(String token) {
        return parseClaims(token, resetKey).getSubject();
    }

    public String extractResetTokenForType(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(resetKey) // Klucz weryfikujący podpis
                    .build()
                    .parseClaimsJws(token) // Parsowanie tokena
                    .getBody(); // Pobranie części payload

            return claims.get("type", String.class); // Wyciągnięcie wartości claim "type"
        } catch (JwtException e) {
            log.error("Invalid token: {}", e.getMessage());
            return null; // Zwrot null w przypadku błędnego tokena
        }
    }

    public List<String> extractRoles(String token) {
        Claims claims = parseClaims(token, secretKey);
        Object roles = claims.get("roles");
        if (roles instanceof List<?> rolesList) {
            return rolesList.stream()
                    .map(Object::toString)
                    .toList();
        }
        return List.of();
    }

    private Claims parseClaims(String token, SecretKey key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            log.error("Failed to parse token: {}", e.getMessage());
            throw e;
        }
    }

}
