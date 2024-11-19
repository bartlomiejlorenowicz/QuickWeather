package com.quickweather.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.quickweather.validator.IntegrationTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WireMockTest(httpPort = 8081)
@ActiveProfiles("test")
class OpenWeatherControllerTest extends IntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Value("${open.weather.api.key}")
    private String apiKey;

    private final String url = "/api/weather";

    @Test
    void shouldReturnWeatherData_WhenCityIsValid() throws Exception {
        String responseBody = new String(Files.readAllBytes(Paths.get("src/test/resources/app/responses/current_weather.json")));

        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("appid", equalTo(apiKey))
                .withQueryParam("units", equalTo("metric"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/city")
                        .param("city", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(15.0))
                .andExpect(jsonPath("$.weather[0].description").value("sunny"))
                .andExpect(jsonPath("$.name").value("London"));
    }

    @Test
    void shouldReturnBadRequest_WhenCityIsBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/city")
                        .param("city", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("City name cannot be blank"));
    }

    @Test
    void shouldReturnNotFound_WhenCityIsUnknown() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/city")
                        .param("city", "UnknownCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data not found for: UnknownCity"));
    }

    @Test
    void shouldReturnWeatherData_WhenZipcodeIsValid() throws Exception {
        String responseBody = new String(Files.readAllBytes(Paths.get("src/test/resources/app/responses/current_weather_by_zipcode.json")));

        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("zip", equalTo("37-203,pl"))
                .willReturn(aResponse()
                        .withBody(responseBody)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/zipcode")
                        .param("zipcode", "37-203")
                        .param("countryCode", "pl")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sys.country").value("PL"))
                .andExpect(jsonPath("$.weather[0].description").value("clear sky"));
    }

    @Test
    void shouldReturnBadRequest_WhenCountryCodeIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/zipcode")
                        .param("zipcode", "37-203")
                        .param("countryCode", "1111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Country  code must be 2 letters"));
    }

    @Test
    void shouldReturnBadRequest_WhenZipcodeIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/zipcode")
                        .param("countryCode", "pl")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request parameter 'zipcode' is missing"));
    }

    @Test
    void shouldReturn5DaysForecast_WhenCityIsValid() throws Exception {
        String weatherResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/app/responses/forecast_for_5_days.json")));

        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/forecast"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("appid", equalTo(apiKey))
                .withQueryParam("units", equalTo("metric"))
                .withQueryParam("lang", equalTo("pl"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(weatherResponse)
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/forecast")
                        .param("city", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].main.temp").value(15.0))
                .andExpect(jsonPath("$.list[0].weather[0].description").value("clear sky"))
                .andExpect(jsonPath("$.city.name").value("Sample City"));
    }

    @Test
    void shouldReturnAirPollutionData_WhenCoordinatesAreValid() throws Exception {
        String responseDto = new String(Files.readAllBytes(Paths.get("src/test/resources/app/responses/air_pollution_response.json")));

        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/air_pollution"))
                .withQueryParam("lat", equalTo("50.0"))
                .withQueryParam("lon", equalTo("50.0"))
                .withQueryParam("appid", equalTo(apiKey))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseDto)
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "50.0")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].main.aqi").value(2.0));
    }

    @Test
    void shouldReturnBadRequest_WhenLatitudeIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to convert value of parameter 'lat' to required type 'double'"));
    }

    @Test
    void shouldReturnBadRequest_WhenLatitudeIsOutOfBounds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "-91")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Latitude must be between -90 and 90"));
    }

    @Test
    void shouldReturnBadRequest_WhenLongitudeIsOutOfBounds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "50.0")
                        .param("lon", "181")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Longitude must be between -180 and 180"));
    }

    @Test
    void shouldReturnForecastByCityAndDays_WhenParametersAreValid() throws Exception {
        String responseDto = new String(Files.readAllBytes(Paths.get("src/test/resources/app/responses/forecast_by_city_and_by_days.json")));

        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/forecast"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("cnt", equalTo("2"))
                .withQueryParam("units", equalTo("metric"))
                .withQueryParam("appid", equalTo(apiKey))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseDto)
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/forecast/daily")
                        .param("city", "London")
                        .param("cnt", "2")
                        .param("units", "metric")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].temp.day").value(20.98))
                .andExpect(jsonPath("$.list[1].temp.day").value(21.54));
    }
}