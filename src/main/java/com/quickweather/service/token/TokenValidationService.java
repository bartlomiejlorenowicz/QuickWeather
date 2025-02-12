package com.quickweather.service.token;

import com.quickweather.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenValidationService {

    private final JwtUtil jwtUtil;
    private static final String RESET_PASSWORD_TOKEN_TYPE = "reset-password";

    public void validateResetTokenOrThrow(String token) {
        if (!jwtUtil.validateResetToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        String tokenType = jwtUtil.extractResetTokenForType(token);
        log.info("Validating token: {}", token);
        if (!RESET_PASSWORD_TOKEN_TYPE.equals(tokenType)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token type");
        }
    }
}
