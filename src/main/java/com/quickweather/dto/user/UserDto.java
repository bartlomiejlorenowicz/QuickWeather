package com.quickweather.dto.user;

import com.quickweather.entity.Role;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

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

    private Set<Role> roles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
