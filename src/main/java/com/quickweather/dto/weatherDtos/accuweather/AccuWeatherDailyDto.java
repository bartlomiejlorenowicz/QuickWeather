package com.quickweather.dto.weatherDtos.accuweather;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccuWeatherDailyDto {
    private String headlineText;
    private List<TemperatureSummaryDto> dailyTemperatures;
}