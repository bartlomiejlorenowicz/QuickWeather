package com.quickweather.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean enabled;
}