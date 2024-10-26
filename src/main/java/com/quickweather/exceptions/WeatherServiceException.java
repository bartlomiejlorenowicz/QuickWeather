package com.quickweather.exceptions;

public class WeatherServiceException extends RuntimeException {
    private final WeatherErrorType weatherErrorType;

    public WeatherServiceException(WeatherErrorType weatherErrorType, String message) {
        super(message);
        this.weatherErrorType = weatherErrorType;
    }

    public WeatherErrorType getWeatherErrorType() {
        return weatherErrorType;
    }
}
