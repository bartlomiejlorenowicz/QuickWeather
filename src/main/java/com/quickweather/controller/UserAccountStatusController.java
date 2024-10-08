package com.quickweather.controller;

import com.quickweather.dto.user.UserId;
import com.quickweather.service.user.UserStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/account-status")
public class UserAccountStatusController {

    private final UserStatusService userStatusService;

    public UserAccountStatusController(UserStatusService userStatusService) {
        this.userStatusService = userStatusService;
    }

    @PutMapping("/enable")
    public ResponseEntity<Void> enableUser(@RequestBody UserId userId) {
        userStatusService.enableUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/disable")
    public ResponseEntity<Void> disableUser(@RequestBody UserId userId) {
        userStatusService.disabledUser(userId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
