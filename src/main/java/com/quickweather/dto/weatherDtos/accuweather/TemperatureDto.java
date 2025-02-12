package com.quickweather.dto.weatherDtos.accuweather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemperatureDto {
    private UnitValueDto minimum;
    private UnitValueDto maximum;
}
