package com.quickweather.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {

    private String firstName;

    private String lastName;

    private String password;

    private String email;

    private String phoneNumber;

    private boolean isEnabled;

    private boolean isLocked;

    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
