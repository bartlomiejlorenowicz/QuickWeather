package com.quickweather.controllers;

import com.quickweather.dto.weatherDtos.accuweather.AccuWeatherResponse;
import com.quickweather.dto.weatherDtos.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.weatherDtos.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.weatherDtos.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weatherDtos.weather.SimpleForecastDto;
import com.quickweather.dto.weatherDtos.weather.WeatherResponse;
import com.quickweather.dto.weatherDtos.weather.WeatherByZipCodeResponseDto;
import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.service.accuweather.AccuWeatherServiceImpl;
import com.quickweather.service.weather.OpenWeatherServiceImpl;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/weather")
@Validated
public class WeatherController {

    private final OpenWeatherServiceImpl currentWeatherService;
    private final AccuWeatherServiceImpl accuWeatherService;

    public WeatherController(OpenWeatherServiceImpl currentWeatherService, AccuWeatherServiceImpl accuWeatherService) {
        this.currentWeatherService = currentWeatherService;
        this.accuWeatherService = accuWeatherService;
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
            @RequestParam @Size(min = 2, max = 2, message = "Country code must be 2 letters") String countryCode) {
        return currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode);
    }

    @GetMapping("/forecast")
    public List<SimpleForecastDto> getForecast(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city) {
        return currentWeatherService.getSimpleForecast(city);
    }

    @GetMapping("/city/air-quality")
    public AirPollutionResponseDto getAirPollutionByCity(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city) {
        WeatherResponse weatherResponse = currentWeatherService.getCurrentWeatherByCity(city);

        if (weatherResponse.getCoord() == null) {
            throw new WeatherServiceException(WeatherErrorType.DATA_NOT_FOUND, "Missing coordinate data for city: " + city);
        }
        double lat = weatherResponse.getCoord().getLat();
        double lon = weatherResponse.getCoord().getLon();

        // Pobierz dane o zanieczyszczeniu powietrza przy użyciu współrzędnych
        return currentWeatherService.getAirPollutionByCoordinates(lat, lon);
    }

    @GetMapping("/forecast/daily")
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city,
            @RequestParam @Min(value = 1, message = "Count must be at least 1")
            @Max(value = 16, message = "Count cannot be more than 16") int cnt) {
        return currentWeatherService.getWeatherForecastByCityAndDays(city, cnt);
    }

    @GetMapping("/coordinate")
    public WeatherResponse getWeatherByCoordinates(@RequestParam String lat,
                                                   @RequestParam String lon) {
        return currentWeatherService.getCurrentWeatherByCoordinates(lat, lon);
    }

    @GetMapping("/postcode")
    public List<AccuWeatherResponse> getLocationByPostalCode(
            @RequestParam String postcode) {
        return accuWeatherService.getLocationByPostalCode(postcode);
    }


}
