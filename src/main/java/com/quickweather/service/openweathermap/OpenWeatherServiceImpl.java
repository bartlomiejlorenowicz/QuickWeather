package com.quickweather.service.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.time.LocalDateTime;
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

    private final UserSearchHistoryService userSearchHistoryService;

    public OpenWeatherServiceImpl(RestTemplate restTemplate,
                                  WeatherApiResponseRepository weatherApiResponseRepository,
                                  ObjectMapper objectMapper,
                                  UserSearchHistoryService userSearchHistoryService) {
        super(restTemplate, weatherApiResponseRepository, objectMapper);
        this.userSearchHistoryService = userSearchHistoryService;
    }

    public void saveWeatherApiResponse(String city, WeatherResponse weatherResponse) {
        WeatherApiResponse weatherApiResponse = new WeatherApiResponse();
        weatherApiResponse.setCity(city);
        weatherApiResponse.setApiSource(ApiSource.OPEN_WEATHER);
        weatherApiResponse.setResponseJson(objectMapper.valueToTree(weatherResponse));
        weatherApiResponse.setRequestJson(objectMapper.valueToTree(weatherResponse));
        weatherApiResponse.setCreatedAt(LocalDateTime.now()); // Ustawienie daty utworzenia

        weatherApiResponseRepository.save(weatherApiResponse);
    }

    @Override
    public WeatherResponse getCurrentWeatherByCity(String city) {
        Optional<WeatherApiResponse> cachedResponse = getCacheWeatherResponse(city, ApiSource.OPEN_WEATHER);
        if (cachedResponse.isPresent()) {
            return processCachedResponse(city, cachedResponse.get());
        }

        return fetchWeatherFromApi(city);
    }

    // helper methods for getCurrentWeatherByCity
    private WeatherResponse processCachedResponse(String city, WeatherApiResponse cachedResponse) {
        log.info("Returning cached response for city {}", city);
        try {
            return objectMapper.treeToValue(cachedResponse.getResponseJson(), WeatherResponse.class);
        } catch (JsonProcessingException e) {
            log.info("Failed to deserialize cached response for city {}: {}", city, e.getMessage());
            throw new WeatherServiceException(WeatherErrorType.SERIALIZATION_ERROR, "Failed to deserialize cached response for: " + city);
        }
    }

    private WeatherResponse fetchWeatherFromApi(String city) {
        log.info("Fetching weather data from API for city {}", city);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_QUERY, city);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put(PARAM_UNITS, "metric");

        log.info("Building API request with params: {}", queryParams);
        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);

        WeatherResponse response = fetchWeatherData(url, WeatherResponse.class, city);

        saveWeatherDataToDatabase(city, response, queryParams);

        return response;
    }

    private void saveWeatherDataToDatabase(String city, WeatherResponse response, Map<String, String> queryParams) {
        try {
            String responseJson = objectMapper.writeValueAsString(response);
            String requestJson = objectMapper.writeValueAsString(queryParams);

            saveWeatherResponse(city, null, ApiSource.OPEN_WEATHER, responseJson, requestJson);
            log.info("API data saved to the database");
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize weather data for city {}: {}", city, e.getMessage());
            throw new WeatherServiceException(WeatherErrorType.SERIALIZATION_ERROR, "Failed to serialize weather data for: " + city);
        }
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
