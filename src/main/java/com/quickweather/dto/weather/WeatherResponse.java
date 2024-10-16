package com.quickweather.dto.weather;

import com.quickweather.dto.weather.Main;
import com.quickweather.dto.weather.Weather;

import java.util.List;

public class WeatherResponse {

    private Main main;
    private List<Weather> weather;
    private String name;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
