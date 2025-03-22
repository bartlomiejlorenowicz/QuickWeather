package com.quickweather.dto.user;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserStatusRequest {
    private boolean enabled;
    private boolean unblock;
    private LocalDateTime lockUntil;
    private int failedAttempts;
}
