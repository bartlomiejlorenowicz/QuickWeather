package com.quickweather.controller;

import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.service.accuweather.AccuWeatherServiceImpl;
import com.quickweather.service.openweathermap.OpenWeatherServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final OpenWeatherServiceImpl currentWeatherService;

    private final AccuWeatherServiceImpl accuWeatherService;

    public WeatherController(OpenWeatherServiceImpl currentWeatherService, AccuWeatherServiceImpl accuWeatherService) {
        this.currentWeatherService = currentWeatherService;
        this.accuWeatherService = accuWeatherService;
    }

    @GetMapping("/city")
    public WeatherResponse getCurrentWeather(@RequestParam String city) {
        return currentWeatherService.getCurrentWeatherByCity(city);
    }

    @GetMapping("/postcode")
    public List<AccuWeatherResponse> getLocationByPostalCode(@RequestParam String postcode) {
        return accuWeatherService.getLocationByPostalCode(postcode);
    }

    @GetMapping("/zipcode")
    public WeatherByZipCodeResponseDto getCurrentWeatherByZipcode(@RequestParam String zipcode, @RequestParam String countryCode) {
        return currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode);
    }

    @GetMapping( "/forecast")
    public HourlyForecastResponseDto get5DaysForecast(@RequestParam String city) {
        return currentWeatherService.get5DaysForecastEvery3Hours(city);
    }

    @GetMapping("/air-quality")
    public AirPollutionResponseDto getAirPollutionByCoordinates(@RequestParam double lat, @RequestParam double lon) {
        return currentWeatherService.getAirPollutionByCoordinates(lat, lon);
    }

    @GetMapping("/forecast/daily")
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(@RequestParam String city, @RequestParam int cnt) {
        return currentWeatherService.getWeatherForecastByCityAndDays(city, cnt);
    }
}
