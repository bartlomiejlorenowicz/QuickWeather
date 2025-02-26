package com.quickweather.dto.weatherDtos.forecast;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.quickweather.dto.weatherDtos.weather.Main;
import com.quickweather.dto.weatherDtos.weather.Temp;
import com.quickweather.dto.weatherDtos.weather.Weather;
import com.quickweather.dto.weatherDtos.weather.Wind;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class ForecastDailyDto {
    private Main main;
    private List<Weather> weather;
    private Wind wind;
    private int visibility;

}
