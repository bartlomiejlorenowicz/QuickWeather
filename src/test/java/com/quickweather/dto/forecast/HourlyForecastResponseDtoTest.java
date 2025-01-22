package com.quickweather.dto.forecast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.location.City;
import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HourlyForecastResponseDtoTest {

    @Test
    void shouldDeserializeJsonToHourlyForecastResponseDto() throws Exception {
        // Given: Ścieżka do pliku JSON
        File jsonFile = new File("src/test/resources/app/responses/forecast_for_5_days.json");

        // And: Obiekt ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();

        // When: Deserializacja JSON do HourlyForecastResponseDto
        HourlyForecastResponseDto forecastResponse = objectMapper.readValue(jsonFile, HourlyForecastResponseDto.class);

        // Then: Weryfikacja zdeserializowanych danych
        assertThat(forecastResponse).isNotNull();

        // Weryfikacja danych miasta
        City city = forecastResponse.getCity();
        assertThat(city).isNotNull();
        assertThat(city.getName()).isEqualTo("London");
        assertThat(city.getCountry()).isEqualTo("GB");

        // Weryfikacja listy prognoz
        assertThat(forecastResponse.getList()).isNotNull();
        assertThat(forecastResponse.getList().size()).isGreaterThan(0);

        // Weryfikacja pierwszego elementu prognozy
        ForecastItem firstItem = forecastResponse.getList().get(0);
        assertThat(firstItem.getDt_txt()).isEqualTo("2024-11-04 12:00:00");
        assertThat(firstItem.getMain().getTemp()).isEqualTo(15.0);
        assertThat(firstItem.getWind().getSpeed()).isEqualTo(3.5);
    }

}