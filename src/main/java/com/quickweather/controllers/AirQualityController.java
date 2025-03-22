package com.quickweather.controllers;

import com.quickweather.dto.weatherDtos.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.weatherDtos.weather.WeatherResponse;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.service.weather.OpenWeatherServiceImpl;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@Validated
@RequiredArgsConstructor
public class AirQualityController {

    private final OpenWeatherServiceImpl currentWeatherService;

    @GetMapping("/air-quality")
    public AirPollutionResponseDto getAirPollutionByCity(
            @RequestParam @NotBlank(message = "City name cannot be blank") String city) {
        return currentWeatherService.getAirPollutionByCity(city);
    }
}
