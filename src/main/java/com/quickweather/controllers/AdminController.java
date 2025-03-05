package com.quickweather.controllers;

import com.quickweather.domain.SecurityEvent;
import com.quickweather.dto.admin.AdminStatsResponse;
import com.quickweather.dto.apiResponse.ApiResponse;
import com.quickweather.dto.apiResponse.OperationType;
import com.quickweather.dto.user.user_auth.ChangePasswordRequest;
import com.quickweather.service.admin.AdminService;
import com.quickweather.service.admin.SecurityEventService;
import com.quickweather.service.user.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    private final SecurityEventService securityEventService;

    private final PasswordService passwordService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getDashboardStats() {
        AdminStatsResponse stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestParam boolean enabled) {
        adminService.updateUserStatus(userId, enabled);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/logs")
    public ResponseEntity<Page<SecurityEvent>> getSecurityEvents(Pageable pageable) {
        Page<SecurityEvent> events = securityEventService.getAllEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse> changeAdminPassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {

        log.info("Authenticated admin: {}", authentication.getName());

        String email = authentication.getName();
        passwordService.changePassword(email, request);

        ApiResponse apiResponse = ApiResponse.buildApiResponse(
                "Password changed successfully. Please log in again.",
                OperationType.CHANGE_PASSWORD
        );

        return ResponseEntity.ok(apiResponse);
    }
}
