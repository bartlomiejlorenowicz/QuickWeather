package com.quickweather.service.user;

import com.google.api.services.gmail.Gmail;
import com.quickweather.domain.User;
import com.quickweather.dto.user.user_auth.SetNewPasswordRequest;
import com.quickweather.exceptions.EmailSendingException;
import com.quickweather.integration.GmailQuickstart;
import com.quickweather.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
public class PasswordResetService {

    private final UserCrudService userCrudService;
    private final JwtUtil jwtUtil;
    private final GmailQuickstart gmailQuickstart;
    private final PasswordEncoder passwordEncoder;
    private final String RESET_EMAIL_SUBJECT = "Password Reset Request";
    private final String CONTENT_EMAIL = "Click the link to reset your password: ";

    @Value("${app.frontend.base-url:http://localhost:4200}")
    private String frontendBaseUrl;

    public PasswordResetService(UserCrudService userCrudService,
                                JwtUtil jwtUtil,
                                GmailQuickstart gmailQuickstart,
                                PasswordEncoder passwordEncoder) {
        this.userCrudService = userCrudService;
        this.jwtUtil = jwtUtil;
        this.gmailQuickstart = gmailQuickstart;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetPasswordUsingToken(SetNewPasswordRequest request) {
        // Walidacja tokena resetu
        if (!jwtUtil.validateResetToken(request.getToken())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
        }

        // Pobierz użytkownika na podstawie tokena
        String email = jwtUtil.extractUsernameFromResetToken(request.getToken());
        User user = userCrudService.findByEmail(email);
        log.info("User found: {}", user != null ? user.getEmail() : "null");

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        // Sprawdzenie, czy nowe hasło i potwierdzenie są takie same
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        // Zmień hasło
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userCrudService.save(user);
    }

    public void sendPasswordResetEmail(String email, String resetPath) {

        User user = userCrudService.findByEmail(email);
        String resetToken = jwtUtil.generateResetToken(user);
        String resetLink = frontendBaseUrl + resetPath + "?token=" + resetToken;

        try {
            Gmail service = gmailQuickstart.getGmailService();
            gmailQuickstart.sendEmail(service, user.getEmail(), RESET_EMAIL_SUBJECT,
                    CONTENT_EMAIL + resetLink);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage(), e);
            throw new EmailSendingException("Failed to send password reset email", e);
        }
    }
}
