package com.quickweather.dto.accuweather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyForecastsDto {
    private String date;
    private TemperatureDto temperature;
}
