package com.quickweather.exceptions;

public class InvalidEmailAlreadyExistException extends RuntimeException {
    public InvalidEmailAlreadyExistException(String message) {
        super(message);
    }
}
