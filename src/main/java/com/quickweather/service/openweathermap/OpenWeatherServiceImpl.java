package com.quickweather.service.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.entity.ApiSource;
import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.repository.WeatherApiResponseRepository;
import com.quickweather.service.weatherbase.WeatherServiceBase;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OpenWeatherServiceImpl extends WeatherServiceBase implements OpenWeatherMapService {

    @Value("${open.weather.api.key}")
    private String apiKey;

    @Value("${open.weather.api.url}")
    private String apiUrl;

    private static final String PARAM_APPID = "appid";
    private static final String PARAM_UNITS = "units";
    private static final String PARAM_QUERY = "q";

    public OpenWeatherServiceImpl(RestTemplate restTemplate,
                                  WeatherApiResponseRepository weatherApiResponseRepository,
                                  ObjectMapper objectMapper) {
        super(restTemplate, weatherApiResponseRepository, objectMapper);
    }

    @Cacheable(value = "weatherData", key = "#city", unless = "#result == null")
    @Override
    public WeatherResponse getCurrentWeatherByCity(String city) {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_QUERY, city);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put(PARAM_UNITS, "metric");

        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);
        log.info("execution request with URL: {} and params: {}", apiUrl, queryParams);
        log.info("with params {}", queryParams);

        WeatherResponse response = fetchWeatherData(url, WeatherResponse.class, city);

        try {
           String responseJson = objectMapper.writeValueAsString(response);

           saveWeatherResponse(city, null, ApiSource.OPEN_WEATHER, responseJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize weather data for city {}: {}", city, e.getMessage());
            throw new WeatherServiceException(WeatherErrorType.SERIALIZATION_ERROR, "Failed to serialize weather data for: " + city);
        }

        return response;
    }

    @Override
    public WeatherByZipCodeResponseDto getCurrentWeatherByZipcode(String zipcode, String countryCode) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("zip", zipcode + "," + countryCode);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put("lang", "pl");

        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);
        return fetchWeatherData(url, WeatherByZipCodeResponseDto.class, zipcode + "," + countryCode);
    }

    @Override
    public HourlyForecastResponseDto get5DaysForecastEvery3Hours(String city) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_QUERY, city);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put(PARAM_UNITS, "metric");
        queryParams.put("lang", "pl");

        URI url = UriBuilderUtils.buildUri(apiUrl, "forecast", queryParams);
        return fetchWeatherData(url, HourlyForecastResponseDto.class, city);
    }

    @Override
    public AirPollutionResponseDto getAirPollutionByCoordinates(double lat, double lon) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("lat", String.valueOf(lat));
        queryParams.put("lon", String.valueOf(lon));
        queryParams.put(PARAM_APPID, apiKey);

        URI url = UriBuilderUtils.buildUri(apiUrl, "air_pollution", queryParams);
        return fetchWeatherData(url, AirPollutionResponseDto.class, lat + "," + lon);
    }

    @Override
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(String city, int cnt) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_QUERY, city);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put("cnt", String.valueOf(cnt));
        queryParams.put(PARAM_UNITS, "metric");

        URI url = UriBuilderUtils.buildUri(apiUrl, "forecast", queryParams);
        return fetchWeatherData(url, WeatherForecastDailyResponseDto.class, city + "," + cnt);
    }
}
