package com.quickweather.controller;

import com.quickweather.dto.accuweather.AccuWeatherDailyResponse;
import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.dto.accuweather.AccuWeatherDailyDto;
import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.mapper.AccuWeatherMapper;
import com.quickweather.dto.weather.SimpleForecastDto;
import com.quickweather.dto.weather.UserSearchHistoryResponse;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.entity.UserSearchHistory;
import com.quickweather.repository.UserSearchHistoryRepository;
import com.quickweather.service.accuweather.AccuWeatherServiceImpl;
import com.quickweather.service.openweathermap.OpenWeatherServiceImpl;
import com.quickweather.service.openweathermap.UserSearchHistoryService;
import com.quickweather.service.user.CustomUserDetails;
import io.jsonwebtoken.Jwt;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weather")
@Validated
public class WeatherController {

    private final OpenWeatherServiceImpl currentWeatherService;

    private final AccuWeatherServiceImpl accuWeatherService;

    private final UserSearchHistoryRepository userSearchHistoryRepository;

    private final UserSearchHistoryService userSearchHistoryService;

    public WeatherController(OpenWeatherServiceImpl currentWeatherService,
                             AccuWeatherServiceImpl accuWeatherService,
                             UserSearchHistoryRepository userSearchHistoryRepository,
                             UserSearchHistoryService userSearchHistoryService) {
        this.currentWeatherService = currentWeatherService;
        this.accuWeatherService = accuWeatherService;
        this.userSearchHistoryRepository = userSearchHistoryRepository;
        this.userSearchHistoryService = userSearchHistoryService;
    }

    @GetMapping("/current-with-user-history")
    public WeatherResponse getWeatherWithHistory(@RequestParam Long userId,
                                                 @RequestParam String city,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {

        WeatherResponse response = getCurrentWeatherByCity(city);

        if (userDetails != null) {
            // Zalogowany użytkownik - zapisujemy dane w UserSearchHistory
            userSearchHistoryService.saveSearchHistory(userId, city, response);
        } else {
            // Niezalogowany użytkownik - zapisujemy dane w WeatherApiResponseHistory
            currentWeatherService.saveWeatherApiResponse(city, response);
        }

        // Zwracamy odpowiedź pogodową
        return response;
    }

    @GetMapping("/history")
    public List<UserSearchHistoryResponse> getUserSearchHistory(@RequestParam Long userId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam (defaultValue = "5") int size) {
        return userSearchHistoryService.getUserSearchHistory(userId, page, size);
    }


    @GetMapping("/city")
    public WeatherResponse getCurrentWeatherByCity(
            @RequestParam
            @NotBlank(message = "City name cannot be blank") String city) {
        return currentWeatherService.getCurrentWeatherByCity(city);
    }

    @GetMapping("/zipcode")
    public WeatherByZipCodeResponseDto getCurrentWeatherByZipcode(
            @RequestParam String zipcode,
            @RequestParam @Size(min = 2, max = 2, message = "Country  code must be 2 letters") String countryCode) {
        return currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode);
    }

    @GetMapping("/forecast")
    public List<SimpleForecastDto> getForecast(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city) {
        HourlyForecastResponseDto forecast = currentWeatherService.get5DaysForecastEvery3Hours(city);

        if (forecast == null || forecast.getList() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Weather forecast not available for the given city");
        }

        // Mapowanie na uproszczony format
        return forecast.getList().stream()
                .map(item -> new SimpleForecastDto(
                        item.getDt_txt(),
                        item.getMain().getTemp(),
                        item.getWind().getSpeed()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/air-quality")
    public AirPollutionResponseDto getAirPollutionByCoordinates(
            @RequestParam @Min(value = -90, message = "Latitude must be between -90 and 90")
            @Max(value = 90, message = "Latitude must be between -90 and 90") double lat,
            @RequestParam @Min(value = -180, message = "Longitude must be between -180 and 180")
            @Max(value = 180, message = "Longitude must be between -180 and 180") double lon) {
        return currentWeatherService.getAirPollutionByCoordinates(lat, lon);
    }

    @GetMapping("/forecast/daily")
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city,
            @RequestParam @Min(value = 1, message = "Count must be at least 1")
            @Max(value = 16, message = "Count cannot be more than 16") int cnt) {
        return currentWeatherService.getWeatherForecastByCityAndDays(city, cnt);
    }

    @GetMapping("/postcode")
    public List<AccuWeatherResponse> getLocationByPostalCode(
            @RequestParam String postcode) {
        return accuWeatherService.getLocationByPostalCode(postcode);
    }

    @GetMapping("/accuweather/forecast/daily/1day")
    public AccuWeatherDailyDto getCustomAccuWeatherForecast(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city) {

        try {
            AccuWeatherResponse locationResponse = accuWeatherService.getLocationByCity(city)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No location found for the city: " + city));

            String locationKey = locationResponse.getKey();

            AccuWeatherDailyResponse forecastResponse = accuWeatherService.getDailyForecastByLocationKey(locationKey);

            return AccuWeatherMapper.mapToCustomWeatherResponseDto(forecastResponse);

        } catch (RuntimeException e) {
            throw e;
        }
    }

}
