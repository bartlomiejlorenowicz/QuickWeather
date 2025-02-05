package com.quickweather.service.weatherbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.entity.ApiSource;
import com.quickweather.entity.WeatherApiResponse;
import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.repository.WeatherApiResponseRepository;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class WeatherServiceBase {

    protected RestTemplate restTemplate;
    protected final WeatherApiResponseRepository weatherApiResponseRepository;
    protected final ObjectMapper objectMapper;

    protected WeatherServiceBase(RestTemplate restTemplate, WeatherApiResponseRepository weatherApiResponseRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.weatherApiResponseRepository = weatherApiResponseRepository;
        this.objectMapper = objectMapper;
    }

    protected <T> T fetchWeatherData(URI url, Class<T> responseType, String identifier) {
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

    //pobiera dane z bazy jesli sa dostepne
    public Optional<WeatherApiResponse> getCacheWeatherResponse(String city, ApiSource apiSource) {
        return weatherApiResponseRepository.findTopByCityAndApiSourceOrderByCreatedAtDesc(city, apiSource);
    }

    //zapisuje JSON do bazy
    public void saveWeatherResponse(String city, String countryCode, ApiSource apiSource, String responseJson, String requestJson) throws JsonProcessingException {

        if (responseJson == null || responseJson.isEmpty()) {
            log.error("Response JSON is null or empty for city: {}", city);
            throw new WeatherServiceException(WeatherErrorType.DATA_NOT_FOUND, "Response JSON is invalid for city: " + city);
        }
        if (requestJson == null || requestJson.isEmpty()) {
            log.error("Request JSON is null or empty for city: {}", city);
            throw new WeatherServiceException(WeatherErrorType.DATA_NOT_FOUND, "Request JSON is invalid for city: " + city);
        }

        JsonNode validatedResponseJson = objectMapper.readTree(responseJson);
        JsonNode validatedRequestJson = objectMapper.readTree(requestJson);

        WeatherApiResponse weatherApiResponse = new WeatherApiResponse();
        weatherApiResponse.setCity(city);
        weatherApiResponse.setCountryCode(countryCode);
        weatherApiResponse.setApiSource(apiSource);
        weatherApiResponse.setRequestJson(validatedRequestJson);
        weatherApiResponse.setResponseJson(validatedResponseJson);
        weatherApiResponse.setCreatedAt(LocalDateTime.now());

        weatherApiResponseRepository.save(weatherApiResponse);
    }

}
