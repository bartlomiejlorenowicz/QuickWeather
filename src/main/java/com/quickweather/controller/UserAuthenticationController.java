package com.quickweather.controller;

import com.google.api.services.gmail.Gmail;
import com.quickweather.dto.user.login.LoginRequest;
import com.quickweather.dto.user_auth.ChangePasswordRequest;
import com.quickweather.dto.user_auth.SetNewPasswordRequest;
import com.quickweather.entity.User;
import com.quickweather.security.JwtUtil;
import com.quickweather.service.email.GmailQuickstart;
import com.quickweather.service.user.CustomUserDetails;
import com.quickweather.service.user.PasswordService;
import com.quickweather.service.user.UserCreationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/user/auth")
public class UserAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserCreationService userCreationService;
    private final GmailQuickstart gmailQuickstart;
    private final PasswordEncoder passwordEncoder;
    private final PasswordService passwordService;
    private final String RESET_PASSWORD_TOKEN_TYPE = "reset-password";

    public UserAuthenticationController(AuthenticationManager authenticationManager,
                                        JwtUtil jwtUtil,
                                        UserCreationService userCreationService,
                                        GmailQuickstart gmailQuickstart,
                                        PasswordEncoder passwordEncoder,
                                        PasswordService passwordService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userCreationService = userCreationService;
        this.gmailQuickstart = gmailQuickstart;
        this.passwordEncoder = passwordEncoder;
        this.passwordService = passwordService;
    }

    // --------------------- LOGIN ---------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null ||
                loginRequest.getEmail().isBlank() || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body("Email and password must not be empty");
        }

        try {
            var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userCreationService.findByEmail(userDetails.getUsername());

            log.info("Hasło użytkownika w UserDetails: {}", userDetails.getPassword());

            CustomUserDetails customUserDetails = new CustomUserDetails(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getEmail(),
                    user.getPassword(),
                    user.isLocked(),
                    user.isEnabled(),
                    user.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleType()))
                            .toList(),
                    user.getUuid()
            );

            Map<String, Object> token = jwtUtil.generateToken(customUserDetails, user.getUuid());
            System.out.println(token);
            // Zwrócenie tokena w odpowiedzi
            return ResponseEntity.ok(token);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        log.info("Request payload: {}", request); // Dodano logowanie całej treści żądania

        String email = request.get("email");
        log.info("User requesting password reset: {}", email);

        // Sprawdź, czy użytkownik istnieje
        User user = userCreationService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // Wygeneruj token resetu hasła
        String resetToken = jwtUtil.generateResetToken(user);
        log.info("Generated reset token: {}", resetToken);

        // Wyślij e-mail z linkiem do resetu hasła
        try {
            Gmail service = new GmailQuickstart().getGmailService();
            String resetLink = "http://localhost:4200/dashboard/change-password?token=" + resetToken;
            gmailQuickstart.sendEmail(service, user.getEmail(), "Password Reset Request",
                    "Click the link to reset your password: " + resetLink);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
        }

        return ResponseEntity.ok("Password reset link sent");
    }

    @PostMapping("/validate-reset-token")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
            }
            String tokenType = jwtUtil.extractTokenForType(token);
            log.info("Validating token: {}", token);

            if (!RESET_PASSWORD_TOKEN_TYPE.equals(tokenType)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid token type");
            }
            return ResponseEntity.ok("Token is valid");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token validation failed");
        }
    }

    @PostMapping("/set-new-password")
    public ResponseEntity<?> setNewPassword(@Valid @RequestBody SetNewPasswordRequest request) {
        // Walidacja tokena resetującego
        if (!jwtUtil.validateResetToken(request.getToken())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid or expired token."));
        }

        // Pobierz użytkownika na podstawie tokena
        String email = jwtUtil.extractUsername(request.getToken());
        User user = userCreationService.findByEmail(email);
        log.info("User found: {}", user != null ? user.getEmail() : "null");

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found."));
        }

        // Sprawdzenie, czy nowe hasło i potwierdzenie są takie same
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Passwords do not match."));
        }

        // Zmień hasło
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userCreationService.save(user);

        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        log.info("Authenticated user: {}", authentication.getName());
        String email = authentication.getName();

        // Wywołujemy logikę zmiany hasła w serwisie
        passwordService.changePassword(email, request);

        // Zwracamy komunikat o powodzeniu zmiany hasła
        return ResponseEntity.ok(Map.of(
                "message", "Password changed successfully. Please log in again."
        ));
    }





}
