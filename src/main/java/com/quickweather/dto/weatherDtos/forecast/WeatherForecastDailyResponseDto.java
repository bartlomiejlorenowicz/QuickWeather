package com.quickweather.dto.weatherDtos.forecast;

import com.quickweather.dto.weatherDtos.location.City;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class WeatherForecastDailyResponseDto {
    private int cnt;
    private List<ForecastDailyDto> list;
    private City city;
}
