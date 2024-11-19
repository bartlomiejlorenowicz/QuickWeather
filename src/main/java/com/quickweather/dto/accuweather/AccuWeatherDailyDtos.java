package com.quickweather.dto.accuweather;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccuWeatherDailyDtos {
    private String headlineText;
    private List<TemperatureSummaryDto> dailyTemperatures;
}