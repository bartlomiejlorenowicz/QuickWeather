package com.quickweather.service.user;

import com.quickweather.dto.user_auth.ChangePasswordRequest;
import com.quickweather.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordService {
    private final UserCreationService userCreationService;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(String email, ChangePasswordRequest request) {
        // Pobierz użytkownika po emailu
        User user = userCreationService.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        // Sprawdzenie poprawności aktualnego hasła
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect.");
        }

        // Sprawdzenie, czy nowe hasło i potwierdzenie są takie same
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        // Zapobiegamy ustawieniu tego samego hasła
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the old password.");
        }

        // Aktualizacja hasła
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userCreationService.save(user);

        // Wyczyszczenie kontekstu bezpieczeństwa, aby wymusić ponowną autentykację
        SecurityContextHolder.clearContext();
        log.info("Password changed successfully for user: {}. Security context cleared.", email);
    }
}
