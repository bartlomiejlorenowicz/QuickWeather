package com.quickweather.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String userId;

    public UserNotFoundException(String userId) {
        super("User with ID " + userId + " not found.");
        this.userId = userId;
    }

    public UserNotFoundException(String userId, String message) {
        super(message);
        this.userId = userId;
    }
}
