package com.quickweather.service.accuweather;

import com.quickweather.dto.weatherDtos.accuweather.AccuWeatherResponse;
import com.quickweather.exceptions.WeatherServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccuWeatherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccuWeatherServiceImpl accuWeatherService;

    private static final String POSTAL_CODE = "37-203";
    private static final String TEST_API_KEY = "test-api-key";
    private static final String API_URL = "https://dataservice.accuweather.com/locations/v1/postalcodes/search";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(accuWeatherService, "apiKey", TEST_API_KEY);
        ReflectionTestUtils.setField(accuWeatherService, "apiUrl", API_URL);
    }

    @Test
    void testCurrentWeatherByPostalCodeCorrectResult() {
        AccuWeatherResponse[] mockAccuWeatherResponse = {new AccuWeatherResponse()};

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(AccuWeatherResponse[].class))).thenReturn(mockAccuWeatherResponse);

        List<AccuWeatherResponse> result = accuWeatherService.getLocationByPostalCode(POSTAL_CODE);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCurrentWeatherByPostalShouldThrowsHttpClientErrorException() {
        HttpClientErrorException mockException = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(AccuWeatherResponse[].class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> accuWeatherService.getLocationByPostalCode(POSTAL_CODE));

        assertTrue(exception.getMessage().contains("Data not found for: " + POSTAL_CODE));
        assertEquals("NOT_FOUND", mockException.getStatusText());
    }

    @Test
    void testGetLocationByPostalCodeGeneralException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(AccuWeatherResponse[].class))).thenThrow(RuntimeException.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accuWeatherService.getLocationByPostalCode(POSTAL_CODE));

        assertTrue(exception.getMessage().contains("An unknown error occurred while fetching weather data for: " + POSTAL_CODE));
    }
}