package com.quickweather.service.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.weatherDtos.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.weatherDtos.forecast.ForecastDailyDto;
import com.quickweather.dto.weatherDtos.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.weatherDtos.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weatherDtos.weather.Coordinates;
import com.quickweather.dto.weatherDtos.weather.SimpleForecastDto;
import com.quickweather.dto.weatherDtos.weather.WeatherResponse;
import com.quickweather.dto.weatherDtos.weather.WeatherByZipCodeResponseDto;
import com.quickweather.domain.ApiSource;
import com.quickweather.domain.WeatherApiResponse;
import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.repository.WeatherApiResponseRepository;
import com.quickweather.utils.UriBuilderUtils;
import com.quickweather.validation.location.CoordinatesValidator;
import com.quickweather.validation.location.CoordinatesValidatorChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private static final String PARAM_LATITUDE = "lat";
    private static final String PARAM_LONGITUDE = "lon";
    private static final String PARAM_LANGUAGE = "lang";
    private static final String PARAM_COUNT = "cnt";

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
        weatherApiResponse.setCreatedAt(LocalDateTime.now());

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
        queryParams.put(PARAM_UNITS, "metric");
        queryParams.put(PARAM_LANGUAGE, "en");

        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);
        return fetchWeatherData(url, WeatherByZipCodeResponseDto.class, zipcode + "," + countryCode);
    }

    @Override
    public HourlyForecastResponseDto get5DaysForecastEvery3Hours(String city) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_QUERY, city);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put(PARAM_UNITS, "metric");
        queryParams.put(PARAM_LANGUAGE, "pl");

        URI url = UriBuilderUtils.buildUri(apiUrl, "forecast", queryParams);
        return fetchWeatherData(url, HourlyForecastResponseDto.class, city);
    }

    @Override
    public AirPollutionResponseDto getAirPollutionByCoordinates(double lat, double lon) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_LATITUDE, String.valueOf(lat));
        queryParams.put(PARAM_LONGITUDE, String.valueOf(lon));
        queryParams.put(PARAM_APPID, apiKey);

        URI url = UriBuilderUtils.buildUri(apiUrl, "air_pollution", queryParams);
        return fetchWeatherData(url, AirPollutionResponseDto.class, lat + "," + lon);
    }

    @Override
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(String city, int cnt) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_QUERY, city);
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put(PARAM_COUNT, String.valueOf(cnt));
        queryParams.put(PARAM_UNITS, "metric");

        URI url = UriBuilderUtils.buildUri(apiUrl, "forecast", queryParams);
        return fetchWeatherData(url, WeatherForecastDailyResponseDto.class, city + "," + cnt);
    }

    public List<SimpleForecastDto> getSimpleForecast(String city) {
        HourlyForecastResponseDto forecast = get5DaysForecastEvery3Hours(city);
        validateForecast(forecast, city);
        return mapToSimpleForecastDto(forecast);
    }

    private void validateForecast(HourlyForecastResponseDto forecast, String city) {
        if (forecast == null || forecast.getList() == null || forecast.getList().isEmpty()) {
            throw new WeatherServiceException(
                    WeatherErrorType.DATA_NOT_FOUND,
                    "Weather forecast not available for the given city: " + city
            );
        }
    }

    private List<SimpleForecastDto> mapToSimpleForecastDto(HourlyForecastResponseDto forecast) {
        return forecast.getList().stream()
                .map(item -> new SimpleForecastDto(
                        item.getDt_txt(),
                        item.getMain().getTemp(),
                        item.getWind().getSpeed()
                ))
                .collect(Collectors.toList());
    }

    private WeatherForecastDailyResponseDto mapToWeatherForecastDailyResponseDto(HourlyForecastResponseDto forecast) {
       WeatherForecastDailyResponseDto dailyResponse = new WeatherForecastDailyResponseDto();

       dailyResponse.setCnt(forecast.getList() != null ? forecast.getList().size() : 0);
       dailyResponse.setCity(forecast.getCity());
       List<ForecastDailyDto> dailyList = forecast.getList().stream().map(
               item -> {
                   ForecastDailyDto dailyDto = new ForecastDailyDto();
                   dailyDto.setMain(item.getMain());
                   dailyDto.setWeather(item.getWeather());
                   dailyDto.setWind(item.getWind());
                   return dailyDto;
               }
       ).collect(Collectors.toList());
       dailyResponse.setList(dailyList);

       return dailyResponse;
    }

    public WeatherResponse getCurrentWeatherByCoordinates(String latStr, String lonStr) {
        Coordinates coordinates = new Coordinates(latStr, lonStr);

        CoordinatesValidator validatorChain = CoordinatesValidatorChain.buildChain();
        validatorChain.validate(coordinates);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put(PARAM_LATITUDE, String.valueOf(coordinates.getLat()));
        queryParams.put(PARAM_LONGITUDE, String.valueOf(coordinates.getLon()));
        queryParams.put(PARAM_APPID, apiKey);
        queryParams.put(PARAM_LANGUAGE, "en");
        queryParams.put(PARAM_UNITS, "metric");

        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);
        return fetchWeatherData(url, WeatherResponse.class, String.format("lat: %f lon: %f", coordinates.getLat(), coordinates.getLon()));
    }
}
