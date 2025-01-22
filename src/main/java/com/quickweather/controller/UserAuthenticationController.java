package com.quickweather.controller;

import com.quickweather.dto.user.login.LoginRequest;
import com.quickweather.entity.User;
import com.quickweather.security.JwtUtil;
import com.quickweather.service.user.CustomUserDetails;
import com.quickweather.service.user.UserCreationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/auth")
public class UserAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserCreationService userCreationService;

    public UserAuthenticationController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserCreationService userCreationService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userCreationService = userCreationService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail().isBlank() || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email and password must not be empty");
        }

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userCreationService.findByEmail(userDetails.getUsername());

            CustomUserDetails customUserDetails = new CustomUserDetails(
                    user.getId().toString(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.isLocked(),
                    user.isEnabled(),
                    user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                            .toList()
            );

            Map<String, Object> token = jwtUtil.generateToken(customUserDetails, user.getUuid());

            // Zwrócenie tokena w odpowiedzi
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
}
