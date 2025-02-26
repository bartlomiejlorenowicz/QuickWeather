package com.quickweather.exceptions;

import lombok.Getter;

@Getter
public class UserChangePasswordValidationException extends RuntimeException {
    private final UserErrorType userErrorType;

    public UserChangePasswordValidationException(UserErrorType userErrorType, String message) {
        super(message);
        this.userErrorType = userErrorType;
    }
}
