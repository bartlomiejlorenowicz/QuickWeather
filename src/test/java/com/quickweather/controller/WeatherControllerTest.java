package com.quickweather.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WireMockTest(httpPort = 8081)
@ActiveProfiles("test")
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${open.weather.api.key}")
    private String apiKey;

    @Value("${open.weather.api.url}")
    private String weatherApiUrl;

    @Test
    void testGetCurrentWeather() throws Exception {
        String responseBody = "{ \"main\": { \"temp\": 15.0 }, \"weather\": [{ \"description\": \"sunny\" }], \"name\": \"London\" }";

        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo("London"))
                .withQueryParam("appid", equalTo(apiKey))
                .withQueryParam("units", equalTo("metric"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/city")
                        .param("city", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.main.temp").value(15.0))
                .andExpect(jsonPath("$.weather[0].description").value("sunny"))
                .andExpect(jsonPath("$.name").value("London"));
    }

}