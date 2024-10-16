package com.quickweather.service.openweathermap;

import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;

public interface OpenWeatherMapService {

    WeatherResponse getCurrentWeatherByCity(String city);

    WeatherByZipCodeResponseDto getCurrentWeatherByZipcode(String zipcode, String countryCode);

    HourlyForecastResponseDto get5DaysForecastEvery3Hours(String city);

    AirPollutionResponseDto getAirPollutionByCoordinates(double lat, double lon);

    WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(String city, int cnt);
}
