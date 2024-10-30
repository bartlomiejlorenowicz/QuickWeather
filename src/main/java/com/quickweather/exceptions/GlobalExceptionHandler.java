package com.quickweather.exceptions;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final String MESSAGE = "message";
    public static final String ERROR_TYPE = "errorType";
    public static final String TIMESTAMP = "timestamp";

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

    @ExceptionHandler(WeatherServiceException.class)
    public ResponseEntity<Map<String, String>> handleWeatherServiceException(WeatherServiceException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE, ex.getMessage());
        response.put(ERROR_TYPE, ex.getWeatherErrorType().name());
        response.put(TIMESTAMP, LocalDateTime.now().toString());

        HttpStatus status = mapErrorTypeToHttpStatus(ex.getWeatherErrorType());
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ConstraintViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put(MESSAGE, ex.getMessage());
        response.put(ERROR_TYPE, ex.getMessage());
        response.put(TIMESTAMP, LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private HttpStatus mapErrorTypeToHttpStatus(WeatherErrorType weatherErrorType) {

        return switch (weatherErrorType) {
            case INVALID_API_KEY -> HttpStatus.UNAUTHORIZED;
            case DATA_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case WEATHER_DATA_UNAVAILABLE -> HttpStatus.SERVICE_UNAVAILABLE;
            case EXTERNAL_API_ERROR -> HttpStatus.BAD_GATEWAY;
            case INVALID_COORDINATES, INVALID_CITY_NAME, INVALID_ZIP_CODE -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

}
