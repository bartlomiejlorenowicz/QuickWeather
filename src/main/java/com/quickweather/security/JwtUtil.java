package com.quickweather.security;
import com.quickweather.service.user.CustomUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits long");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Map<String, Object> generateToken(CustomUserDetails userDetails, UUID uuid) {
        String token = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(uuid.toString())
                .claim("userId", userDetails.getUserId())
                .claim("name", userDetails.getName())
                .claim("email", userDetails.getEmail())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey, SignatureAlgorithm.HS256) // Podpis tokena
                .compact();

        log.info("User authorities: {}", userDetails.getAuthorities());

        Map<String, Object> tokenResponse = new HashMap<>();
        tokenResponse.put("token", token);
        tokenResponse.put("expiresAt", new Date(System.currentTimeMillis() + expirationTime));
        tokenResponse.put("email", userDetails.getEmail());

        return tokenResponse;
    }


    public String extractUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            log.error("Failed to extract username from token: {}", e.getMessage());
            throw e;
        }
    }

    public List<String> extractRoles(String token) {
        try {
            Object roles = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles");

            // Bezpieczna konwersja JSON array na List<String>
            if (roles instanceof List<?> rolesList) {
                return rolesList.stream()
                        .map(Object::toString) // Konwersja elementów do String
                        .toList();
            }

            return List.of(); // Jeśli roles nie istnieje, zwróć pustą listę
        } catch (JwtException e) {
            log.error("Failed to extract roles from token: {}", e.getMessage());
            throw e;
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", String.class); // Pobierz wartość userId jako String
        } catch (JwtException e) {
            log.error("Failed to extract userId from token: {}", e.getMessage());
            throw e;
        }
    }

}
