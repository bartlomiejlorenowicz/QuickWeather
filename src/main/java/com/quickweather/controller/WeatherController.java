package com.quickweather.controller;

import com.quickweather.dto.accuweather.AccuWeatherDailyResponse;
import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.dto.accuweather.AccuWeatherDailyDto;
import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.mapper.AccuWeatherMapper;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.service.accuweather.AccuWeatherServiceImpl;
import com.quickweather.service.openweathermap.OpenWeatherServiceImpl;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
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
            @RequestParam @Size(min = 2, max = 2, message = "Country  code must be 2 letters") String countryCode) {
        return currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode);
    }

    @GetMapping("/forecast")
    public HourlyForecastResponseDto get5DaysForecast(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city) {
        return currentWeatherService.get5DaysForecastEvery3Hours(city);
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
