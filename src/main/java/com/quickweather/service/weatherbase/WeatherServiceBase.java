package com.quickweather.service.weatherbase;

import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
public abstract class WeatherServiceBase {

    protected RestTemplate restTemplate;

    protected WeatherServiceBase(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected  <T> T fetchWeatherData(URI url, Class<T> responseType, String identifier) {
        try {
            return restTemplate.getForObject(url, responseType);
        } catch (HttpClientErrorException e) {
            handleHttpClientError(e, identifier);
        } catch (Exception e) {
            log.error("An unknown error occurred while fetching weather data for {}: {}", identifier, e.getMessage());
            throw new WeatherServiceException(WeatherErrorType.UNKNOWN_ERROR, "An unknown error occurred while fetching weather data for: " + identifier);
        }
        throw new UnsupportedOperationException("Fetching weather data is unsupported.");
    }

    protected void handleHttpClientError(HttpClientErrorException e, String identifier) {
        log.error("HTTP error fetching weather data for {}: {}", identifier, e.getMessage());

        if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new WeatherServiceException(WeatherErrorType.INVALID_API_KEY, "Invalid API key");
        } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new WeatherServiceException(WeatherErrorType.DATA_NOT_FOUND, "Data not found for: " + identifier);
        } else if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
            throw new WeatherServiceException(WeatherErrorType.WEATHER_DATA_UNAVAILABLE, "Weather service unavailable");
        } else {
            throw new WeatherServiceException(WeatherErrorType.EXTERNAL_API_ERROR, "Error fetching weather data for: " + identifier);
        }
    }
}
