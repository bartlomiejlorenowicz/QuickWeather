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

import java.nio.file.Files;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WireMockTest(httpPort = 8081)
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
public class AccuWeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Value("${accuweather.api.key.test}")
    private String accuApiKey;

    @Value("${accuweather.api.url.test}")
    private String accuApiUrl;

    @Test
    void shouldReturnLocationByPostalCode_WhenPostcodeIsValid() throws Exception {
        String responseBody = new String(Files.readAllBytes(Paths.get("src/test/resources/app/responses/location_by_postcode.json")));
        System.out.println("Stub URL: " + accuApiUrl);
        System.out.println("Stub API Key: " + accuApiKey);

        stubFor(WireMock.get(urlPathEqualTo(accuApiUrl))
                .withQueryParam("apikey", equalTo(accuApiKey))
                .withQueryParam("q", equalTo("37-203"))
                .withQueryParam("language", equalTo("en-us"))
                .willReturn(aResponse()
                        .withBody(responseBody)
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/weather/postcode")
                        .param("postcode","37-203")
                        .param("language", "en-us")
                        .param("offset", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].LocalizedName").value("Gniewczyna Lancucka"))
                .andExpect(jsonPath("$[0].Region.LocalizedName").value("Europe"))
                .andExpect(jsonPath("$[0].Country.LocalizedName").value("Poland"));
    }
}
