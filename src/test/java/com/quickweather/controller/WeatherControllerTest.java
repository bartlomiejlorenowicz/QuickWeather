package com.quickweather.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.github.tomakehurst.wiremock.client.WireMock.*;


import com.quickweather.service.openweathermap.OpenWeatherServiceImpl;
import com.quickweather.dto.weather.WeatherResponse;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8080) // Use WireMock
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private OpenWeatherServiceImpl openWeatherService;

    @Test
    void testGetCurrentWeather() throws Exception {
        String responseBody = "{ \"main\": { \"temp\": 15.0 }, \"weather\": [{ \"description\": \"sunny\" }], \"name\": \"London\" }";

        stubFor(WireMock.get(urlPathEqualTo("https://api.openweathermap.org/data/2.5/weather"))
                .withQueryParam("q", equalTo("London"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)
                        .withStatus(200)));

        mockMvc.perform(get("https://api.openweathermap.org/data/2.5/weather")
                        .param("city", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(15.0))
                .andExpect(jsonPath("$.weather[0].description").value("sunny"))
                .andExpect(jsonPath("$.name").value("London"));

    }
}