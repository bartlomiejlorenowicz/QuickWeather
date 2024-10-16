package com.quickweather.service.accuweather;

import com.quickweather.dto.accuweather.AccuWeatherResponse;

import java.util.List;

public interface AccuWeatherService {

    List<AccuWeatherResponse> getLocationByPostalCode(String postcode);
}
