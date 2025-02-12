package com.quickweather.dto.weatherDtos.forecast;


import com.quickweather.dto.weatherDtos.location.City;

import java.util.List;

public class HourlyForecastResponseDto {
    private City city;
    private List<ForecastItem> list;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<ForecastItem> getList() {
        return list;
    }

    public void setList(List<ForecastItem> list) {
        this.list = list;
    }
}
