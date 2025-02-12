package com.quickweather.dto.weatherDtos.weather;

import com.quickweather.dto.weatherDtos.location.Coord;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WeatherResponse {

    private Main main;
    private List<Weather> weather;
    private String name;
    private Coord coord;
    private int visibility;
    private Wind wind;

}
