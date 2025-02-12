package com.quickweather.service.accuweather;

import com.quickweather.dto.weatherDtos.accuweather.AccuWeatherResponse;

import java.util.List;

public interface AccuWeatherService {

    List<AccuWeatherResponse> getLocationByPostalCode(String postcode);

    List<AccuWeatherResponse> getLocationByCity(String city);
}
