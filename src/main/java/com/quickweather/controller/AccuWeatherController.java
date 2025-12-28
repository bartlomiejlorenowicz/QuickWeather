package com.quickweather.controller;

import com.quickweather.dto.accuweather.AccuWeatherDailyDto;
import com.quickweather.dto.accuweather.AccuWeatherDailyResponse;
import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.dto.mapper.AccuWeatherMapper;
import com.quickweather.service.accuweather.AccuWeatherServiceImpl;
import jakarta.validation.constraints.NotBlank;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/weather/accuweather")
@Profile("!docker")
public class AccuWeatherController {

    private final AccuWeatherServiceImpl accuWeatherService;

    public AccuWeatherController(AccuWeatherServiceImpl accuWeatherService) {
        this.accuWeatherService = accuWeatherService;
    }


    @GetMapping("/postcode")
    public List<AccuWeatherResponse> getLocationByPostalCode(
            @RequestParam String postcode) {
        return accuWeatherService.getLocationByPostalCode(postcode);
    }

    @GetMapping("/forecast/daily/1day")
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
