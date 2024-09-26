package com.quickweather.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String MESSAGE = "message";
    public static final String ERROR_TYPE = "errorType";

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<Map<String, String>> handlerUserValidationException(UserValidationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE, ex.getMessage());
        response.put(ERROR_TYPE, ex.getUserErrorType().name());

        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex.getUserErrorType() == UserErrorType.EMAIL_ALREADY_EXISTS) {
            status = HttpStatus.CONFLICT;
        }
        return ResponseEntity.status(status).body(response);
    }

}
