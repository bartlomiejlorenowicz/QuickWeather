package com.quickweather.dto.weather;

import com.quickweather.dto.location.Coord;
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
