package com.quickweather.controllers;

import com.quickweather.domain.User;
import com.quickweather.dto.apiResponse.ApiResponse;
import com.quickweather.dto.apiResponse.LoginResponse;
import com.quickweather.dto.apiResponse.OperationType;
import com.quickweather.dto.token.TokenRequest;
import com.quickweather.dto.user.EmailRequest;
import com.quickweather.dto.user.login.LoginRequest;
import com.quickweather.dto.user.user_auth.ChangePasswordRequest;
import com.quickweather.dto.user.user_auth.SetNewPasswordRequest;
import com.quickweather.repository.UserRepository;
import com.quickweather.security.JwtUtil;
import com.quickweather.service.token.TokenValidationService;
import com.quickweather.service.user.CustomUserDetails;
import com.quickweather.service.user.PasswordResetService;
import com.quickweather.service.user.PasswordService;
import com.quickweather.service.user.UserLoginAttemptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/user/auth")
public class UserAuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordService passwordService;
    private final PasswordResetService passwordResetService;
    private final TokenValidationService tokenValidationService;
    private final UserRepository userRepository;
    private final UserLoginAttemptService userLoginAttemptService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        if (loginRequest.getEmail().isBlank() || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (userLoginAttemptService.isAccountLocked(user)) {
                return ResponseEntity.status(HttpStatus.LOCKED)
                        .body(new LoginResponse(OperationType.LOGIN_FAILED, "Your account is locked for 15 minutes. Please try again later."));
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            userLoginAttemptService.resetFailedAttempts(loginRequest.getEmail());

            CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
            Map<String, Object> token = jwtUtil.generateToken(customUserDetails, customUserDetails.getUuid());
            LoginResponse response = LoginResponse.fromTokenMap(token, customUserDetails);
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            userLoginAttemptService.incrementFailedAttempts(loginRequest.getEmail());
            log.error("Authentication failed for user: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody EmailRequest emailRequest) {
        log.info("Reset password requested for: {}", emailRequest.getEmail());

        passwordResetService.sendPasswordResetEmail(emailRequest.getEmail(), "/dashboard/change-password");

        ApiResponse apiResponse = ApiResponse.buildApiResponse("Password reset link sent", OperationType.RESET_PASSWORD);

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody EmailRequest emailRequest) {
        log.info("Forgot password requested for: {}", emailRequest.getEmail());

        passwordResetService.sendPasswordResetEmail(emailRequest.getEmail(), "/set-forgot-password");

        ApiResponse apiResponse = ApiResponse.buildApiResponse("Password reset link sent", OperationType.FORGOT_PASSWORD);

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/validate-reset-token")
    public ResponseEntity<ApiResponse> validateResetToken(@Valid @RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        log.info("Validating reset token: '{}'", token);

        tokenValidationService.validateResetTokenOrThrow(token);
        ApiResponse response = ApiResponse.buildApiResponse("Token is valid", OperationType.VALIDATE_RESET_TOKEN);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/set-new-password")
    public ResponseEntity<ApiResponse> setNewPassword(@Valid @RequestBody SetNewPasswordRequest request) {

        passwordResetService.resetPasswordUsingToken(request);

        ApiResponse apiResponse = ApiResponse.buildApiResponse(
                "Password updated successfully.", OperationType.SET_NEW_PASSWORD);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication authentication) {
        log.info("Authenticated user: {}", authentication.getName());
        String email = authentication.getName();

        passwordService.changePassword(email, request);

        ApiResponse apiResponse = ApiResponse.buildApiResponse("Password changed successfully. Please log in again.", OperationType.CHANGE_PASSWORD);

        return ResponseEntity.ok(apiResponse);
    }

}
