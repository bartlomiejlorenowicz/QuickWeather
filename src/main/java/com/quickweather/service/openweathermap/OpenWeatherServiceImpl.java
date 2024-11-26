package com.quickweather.service.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.entity.ApiSource;
import com.quickweather.entity.WeatherApiResponse;
import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.repository.WeatherApiResponseRepository;
import com.quickweather.service.weatherbase.WeatherServiceBase;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    @Override
    public WeatherResponse getCurrentWeatherByCity(String city) {

        Optional<WeatherApiResponse> cachedResponse = getCacheWeatherResponse(city, ApiSource.OPEN_WEATHER);

        if (cachedResponse.isPresent()) {
            log.info("Returning cached response for city: {}", city);

            try {
                JsonNode responseJsonNode = cachedResponse.get().getResponseJson();
                return objectMapper.treeToValue(responseJsonNode, WeatherResponse.class);
            } catch (JsonProcessingException e) {
                log.error("Failed to deserialize cached response for city {}: {}", city, e.getMessage());
                throw new WeatherServiceException(WeatherErrorType.SERIALIZATION_ERROR, "Failed to deserialize cached response for: " + city);
            }
        }

        log.info("Start asking API");
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
           String requestJson = objectMapper.writeValueAsString(queryParams);

           saveWeatherResponse(city, null, ApiSource.OPEN_WEATHER, responseJson, requestJson);
           log.info("API data saved to the database");
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize weather data for city {}: {}", city, e.getMessage());
            throw new WeatherServiceException(WeatherErrorType.SERIALIZATION_ERROR, "Failed to serialize weather data for: " + city);
        }
        log.info("retrieved from database");
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
