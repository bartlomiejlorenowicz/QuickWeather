package com.quickweather.exceptions;

public class UserValidationException extends RuntimeException {

    private final UserErrorType userErrorType;

    public UserValidationException(UserErrorType userErrorType, String message) {
        super(message);
        this.userErrorType = userErrorType;
    }

    public UserErrorType getUserErrorType() {
        return userErrorType;
    }
}
