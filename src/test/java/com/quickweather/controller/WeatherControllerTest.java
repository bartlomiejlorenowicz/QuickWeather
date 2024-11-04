package com.quickweather.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.quickweather.exceptions.WeatherErrorType;
import com.quickweather.exceptions.WeatherServiceException;
import com.quickweather.service.accuweather.AccuWeatherServiceImpl;
import com.quickweather.service.openweathermap.OpenWeatherServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.mockito.Mockito.when;
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

    @Mock
    private OpenWeatherServiceImpl openWeatherService;

    @Mock
    private AccuWeatherServiceImpl accuWeatherService;

    @InjectMocks
    private WeatherController weatherController;

    private final String url = "/api/weather";

    @Test
    void testGetCurrentWeather_ShouldReturnCorrect() throws Exception {
        String responseBody = "{ \"main\": { \"temp\": 15.0 }, \"weather\": [{ \"description\": \"sunny\" }], \"name\": \"London\" }";

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
    void testGetCurrentWeather_ShouldReturnBadRequest_WhenCityIsBlank() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/city")
                        .param("city", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("City name cannot be blank"));
    }

    @Test
    void testGetCurrentWeather_ShouldReturnNotFound_WhenDataNotFound() throws Exception {
        when(openWeatherService.getCurrentWeatherByCity("UnknownCity"))
                .thenThrow(new WeatherServiceException(WeatherErrorType.DATA_NOT_FOUND, "Data not found for: UnknownCity"));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/city")
                        .param("city", "UnknownCity")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data not found for: UnknownCity"));
    }

    @Test
    void testGetCurrentWeatherByZipcode_ShouldReturnOk_WhenValidRequest() throws Exception {
        String responseBody = "{ \"sys\": { \"country\": \"PL\" }, \"weather\":[{ \"description\": \"clear sky\" }] }";

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
    void testGetCurrentWeatherByZipcode_ShouldReturnBadRequest_WhenCountryCodeIsInvalid() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/zipcode")
                        .param("zipcode", "37-203")
                        .param("countryCode", "1111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Country  code must be 2 letters"));
    }

    @Test
    void testGetCurrentWeatherByZipcode_ShouldReturnBadRequest_WhenZipcodeIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/zipcode")
                        .param("countryCode", "pl")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required request parameter 'zipcode' is missing"));
    }

    @Test
    void testGet5DaysForecast_ShouldReturnCorrect() throws Exception {
        String weatherResponse = """
                {
                  "city": {
                    "id": 123,
                    "name": "Sample City",
                    "country": "PL"
                  },
                  "list": [
                    {
                      "dt": 1672459200,
                      "main": {
                        "temp": 15.0,
                        "feels_like": 14.0,
                        "temp_min": 13.5,
                        "temp_max": 16.0,
                        "pressure": 1012,
                        "humidity": 78
                      },
                      "weather": [
                        {
                          "main": "Clear",
                          "description": "clear sky",
                          "icon": "01d"
                        }
                      ],
                      "wind": {
                        "speed": 3.5,
                        "deg": 180
                      },
                      "clouds": {
                        "all": 10
                      },
                      "dt_txt": "2024-11-04 12:00:00"
                    }
                  ]
                }
                """;
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
    void testGetAirPollutionByCoordinates_ShouldReturnCorrect() throws Exception {

        String responseDto = """
                {
                    "list": [
                        {
                            "main": {
                                "aqi": 2.0
                            },
                            "components": {
                                "co": "220.3",
                                "no": "0",
                                "no2": "0.25",
                                "o3": "77.96",
                                "so2": "0.03",
                                "pm2_5": "0.5",
                                "pm10": "0.5",
                                "nh3": "0.03"
                            }
                        }
                    ]
                }
                """;

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
    void testGetAirPollutionByCoordinates_ShouldReturnBadRequest_WhenLatSizeIsLessThan2() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to convert value of parameter 'lat' to required type 'double'"));
    }

    @Test
    void testGetAirPollutionByCoordinates_ShouldReturnBadRequest_WhenLatIsLessThanMinus90() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "-91")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Latitude must be between -90 and 90"));
    }

    @Test
    void testGetAirPollutionByCoordinates_ShouldReturnBadRequest_WhenLonIsGreaterThan180() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .param("lat", "50.0")
                        .param("lon", "181")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Longitude must be between -180 and 180"));
    }

    @Test
    void testGetWeatherForecastByCityAndDays_ShouldReturnCorrect() throws Exception {

        String responseDto = """
                 {
                          "city": {
                              "id": 4517009,
                              "name": "London",
                              "country": "US"
                          },
                          "list": [
                              {
                                  "temp": {
                                      "day": 20.98,
                                      "min": 18.5,
                                      "max": 23.1,
                                      "night": 17.2,
                                      "eve": 19.3,
                                      "morn": 16.8
                                  },
                                  "pressure": 1019,
                                  "humidity": 51,
                                  "weather": [
                                      {
                                          "main": "Clouds",
                                          "description": "scattered clouds",
                                          "icon": "03d"
                                      }
                                  ]
                              },
                              {
                                  "temp": {
                                      "day": 21.54,
                                      "min": 19.2,
                                      "max": 24.0,
                                      "night": 18.3,
                                      "eve": 20.0,
                                      "morn": 17.5
                                  },
                                  "pressure": 1019,
                                  "humidity": 52,
                                  "weather": [
                                      {
                                          "main": "Clouds",
                                          "description": "broken clouds",
                                          "icon": "04d"
                                      }
                                  ]
                              }
                          ]
                      }
                """;

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