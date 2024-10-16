package com.quickweather.dto.forecast;

import com.quickweather.dto.location.City;

import java.util.List;

public class WeatherForecastDailyResponseDto {
    private City city;
    private List<ForecastDailyDto> list;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<ForecastDailyDto> getList() {
        return list;
    }

    public void setList(List<ForecastDailyDto> list) {
        this.list = list;
    }
}
