package com.quickweather.service.user;

import com.quickweather.domain.User;
import com.quickweather.dto.apiResponse.LoginResponse;
import com.quickweather.dto.user.login.LoginRequest;
import com.quickweather.repository.UserRepository;
import com.quickweather.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserLoginAttemptService userLoginAttemptService;

    public LoginResponse login(LoginRequest loginRequest) {
        if (loginRequest.getEmail().isBlank() || loginRequest.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password must not be blank");
        }

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userLoginAttemptService.isAccountLocked(user)) {
                throw new ResponseStatusException(HttpStatus.LOCKED, "Your account is locked for 15 minutes. Please try again later.");
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            userLoginAttemptService.resetFailedAttempts(loginRequest.getEmail());
            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Map<String, Object> tokenMap = jwtUtil.generateToken(customUserDetails, customUserDetails.getUuid());
            return LoginResponse.fromTokenMap(tokenMap, customUserDetails);
        } catch (AuthenticationException e) {
            userLoginAttemptService.incrementFailedAttempts(loginRequest.getEmail());
            log.error("Authentication failed for user: {}", loginRequest.getEmail());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }
}
