package com.quickweather.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.quickweather.entity.Role;
import com.quickweather.entity.RoleType;
import com.quickweather.entity.User;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;
import com.quickweather.security.JwtTestUtil;
import com.quickweather.security.TestConfig;
import com.quickweather.validator.IntegrationTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WireMockTest(httpPort = 8081)
@Import(TestConfig.class)
@SpringBootTest
class OpenWeatherControllerTest extends IntegrationTestConfig {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private String tokenUser;
    private String tokenAdmin;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Inicjalizacja ról w bazie, jeśli nie istnieją
        if (!roleRepository.existsByRoleType(RoleType.USER)) {
            Role userRole = Role.builder().roleType(RoleType.USER).build();
            roleRepository.save(userRole);
        }
        if (!roleRepository.existsByRoleType(RoleType.ADMIN)) {
            Role adminRole = Role.builder().roleType(RoleType.ADMIN).build();
            roleRepository.save(adminRole);
        }

        Role userRole = roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new IllegalStateException("USER role not initialized in the database"));

        Role adminRole = roleRepository.findByRoleType(RoleType.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not initialized in the database"));

        // Utworzenie użytkownika
        User user = User.builder()
                .firstName("Adam")
                .lastName("Nowak")
                .email("testUser@wp.pl")
                .password(passwordEncoder.encode("testPassword"))
                .isEnabled(true)
                .roles(Set.of(userRole, adminRole))
                .build();

        userRepository.save(user);

        // Generowanie tokenów JWT
        tokenUser = jwtTestUtil.generateToken(user.getEmail(), "ROLE_USER");
        tokenAdmin = jwtTestUtil.generateToken("adminUser", "ROLE_ADMIN");
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

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
                        .header("Authorization", "Bearer " + tokenUser)
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
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("city", "")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("City name cannot be blank"));
    }

    @Test
    void shouldReturnNotFound_WhenCityIsUnknown() throws Exception {
        stubFor(WireMock.get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo("UnknownCity"))
                .withQueryParam("appid", equalTo(apiKey))
                .withQueryParam("units", equalTo("metric"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("{\"message\": \"city not found\"}")));

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/city")
                        .header("Authorization", "Bearer " + tokenUser)
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
                        .header("Authorization", "Bearer " + tokenUser)
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
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("zipcode", "37-203")
                        .param("countryCode", "1111")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Country  code must be 2 letters"));
    }

    @Test
    void shouldReturnBadRequest_WhenZipcodeIsMissing() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/zipcode")
                        .header("Authorization", "Bearer " + tokenUser)
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
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("city", "London")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].date").value("2024-11-04 12:00:00"))
                .andExpect(jsonPath("$[0].temperature").value(15.0))
                .andExpect(jsonPath("$[0].windSpeed").value(3.5));
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
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("lat", "50.0")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].main.aqi").value(2.0));
    }

    @Test
    void shouldReturnBadRequest_WhenLatitudeIsEmpty() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("lat", "")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Failed to convert value of parameter 'lat' to required type 'double'"));
    }

    @Test
    void shouldReturnBadRequest_WhenLatitudeIsOutOfBounds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("lat", "-91")
                        .param("lon", "50.0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Latitude must be between -90 and 90"));
    }

    @Test
    void shouldReturnBadRequest_WhenLongitudeIsOutOfBounds() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/air-quality")
                        .header("Authorization", "Bearer " + tokenUser)
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
                        .header("Authorization", "Bearer " + tokenUser)
                        .param("city", "London")
                        .param("cnt", "2")
                        .param("units", "metric")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list[0].temp.day").value(20.98))
                .andExpect(jsonPath("$.list[1].temp.day").value(21.54));
    }
}