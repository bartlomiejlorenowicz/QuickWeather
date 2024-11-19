package com.quickweather.dto.accuweather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnitValueDto {
    private double value;
    private String unit;
    private int unitType;
}
