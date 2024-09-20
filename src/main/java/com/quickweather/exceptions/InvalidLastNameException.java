package com.quickweather.exceptions;

public class InvalidLastNameException extends RuntimeException {
    public InvalidLastNameException(String message) {
        super(message);
    }
}
