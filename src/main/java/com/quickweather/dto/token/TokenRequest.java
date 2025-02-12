package com.quickweather.dto.token;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {
    @NotBlank(message = "Token must not be blank")
    private String token;
}
