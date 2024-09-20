package com.quickweather.exceptions;

public class InvalidFirstNameException extends RuntimeException {
    public InvalidFirstNameException(String message) {
        super(message);
    }
}
