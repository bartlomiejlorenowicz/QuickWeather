package com.quickweather.exceptions;

public enum WeatherErrorType {

    INVALID_API_KEY,
    DATA_NOT_FOUND,
    BAD_REQUEST,
    WEATHER_DATA_UNAVAILABLE,
    INVALID_CITY_NAME,
    INVALID_ZIP_CODE,
    UNKNOWN_ERROR,
    EXTERNAL_API_ERROR,
    INVALID_COORDINATES
}