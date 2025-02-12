package com.quickweather.service.user;

import com.quickweather.dto.user.user_auth.ChangePasswordRequest;
import com.quickweather.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Provides functionality to change a user's password.
 * <p>
 * The process includes:
 * <ul>
 *   <li>Retrieving the user by email (throws 404 if the user is not found).</li>
 *   <li>Validating the current password (throws 401 if incorrect).</li>
 *   <li>Checking the new password against the confirmation field (throws 400 if they differ).</li>
 *   <li>Ensuring the new password is not the same as the current one.</li>
 *   <li>Encoding and saving the new password.</li>
 *   <li>Clearing the security context to force re-authentication.</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PasswordService {

    private final UserCrudService userCrudService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Changes the password for a user identified by the given email.
     *
     * @param email   The email of the user requesting the password change.
     * @param request A data transfer object containing the current password,
     *                the new password, and its confirmation.
     * @throws ResponseStatusException If the user is not found (404),
     *                                 if the current password is invalid (401),
     *                                 if the new passwords do not match (400),
     *                                 or if the new password is the same as the old one (400).
     */
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userCrudService.findByEmail(email);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Current password is incorrect.");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passwords do not match.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New password must be different from the old password.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userCrudService.save(user);

        SecurityContextHolder.clearContext();
        log.info("Password changed successfully for user: {}. Security context cleared.", email);
    }
}
