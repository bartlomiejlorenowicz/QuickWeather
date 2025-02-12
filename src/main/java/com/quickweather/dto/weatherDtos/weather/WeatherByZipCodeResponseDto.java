package com.quickweather.dto.weatherDtos.weather;

import com.quickweather.dto.weatherDtos.location.SysDto;

import java.util.List;

public class WeatherByZipCodeResponseDto {

    private SysDto sys;
    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }

    public SysDto getSys() {
        return sys;
    }

    public void setSys(SysDto sys) {
        this.sys = sys;
    }
}
