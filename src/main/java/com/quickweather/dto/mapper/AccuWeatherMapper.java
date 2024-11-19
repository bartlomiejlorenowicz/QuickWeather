package com.quickweather.dto.mapper;

import com.quickweather.dto.accuweather.*;
import java.util.stream.Collectors;

public class AccuWeatherMapper {

    public static CustomWeatherResponseDto mapToCustomWeatherResponseDto(AccuWeatherDailyResponse response) {
        CustomWeatherResponseDto customResponse = new CustomWeatherResponseDto();

        if (response.getHeadline() != null) {
            customResponse.setHeadlineText(response.getHeadline().getText());
        }

        if (response.getDailyForecasts() != null) {
            customResponse.setDailyTemperatures(
                    response.getDailyForecasts().stream().map(forecast -> {
                        TemperatureSummaryDto tempSummary = new TemperatureSummaryDto();
                        if (forecast.getTemperature() != null) {
                            if (forecast.getTemperature().getMinimum() != null) {
                                tempSummary.setMinimumValue(forecast.getTemperature().getMinimum().getValue());
                            }
                            if (forecast.getTemperature().getMaximum() != null) {
                                tempSummary.setMaximumValue(forecast.getTemperature().getMaximum().getValue());
                            }
                        }
                        return tempSummary;
                    }).collect(Collectors.toList())
            );
        }

        return customResponse;
    }
}
