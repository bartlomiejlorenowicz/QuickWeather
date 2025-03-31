package com.quickweather.dto.weatherDtos.weather;

import com.quickweather.domain.ApiSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WeatherResponseData {
    private String city;
    private String countryCode;
    private ApiSource apiSource;
    private String responseJson;
    private String requestJson;
}
