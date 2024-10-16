package com.quickweather.service.accuweather;

import com.quickweather.dto.accuweather.AccuWeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
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

    private final String postalCode = "37-203";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field apiKey = AccuWeatherServiceImpl.class.getDeclaredField("apiKey");
        apiKey.setAccessible(true);
        apiKey.set(accuWeatherService, "test-api-key");

        Field apiUrl = AccuWeatherServiceImpl.class.getDeclaredField("apiUrlPostalCode");
        apiUrl.setAccessible(true);
        apiUrl.set(accuWeatherService, "https://dataservice.accuweather.com/locations/v1/postalcodes/search");
    }

    @Test
    void testCurrentWeatherByPostalCodeCorrectResult() {
        AccuWeatherResponse[] mockAccuWeatherResponse = {new AccuWeatherResponse()};

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(AccuWeatherResponse[].class))).thenReturn(mockAccuWeatherResponse);

        List<AccuWeatherResponse> result = accuWeatherService.getLocationByPostalCode(postalCode);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testCurrentWeatherByPostalShouldThrowsHttpClientErrorException() {
        HttpClientErrorException mockException = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(AccuWeatherResponse[].class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accuWeatherService.getLocationByPostalCode(postalCode));

        assertTrue(exception.getMessage().contains("Error fetching postal code: " + postalCode));
        assertEquals("NOT_FOUND", mockException.getStatusText());
    }

    @Test
    void testGetLocationByPostalCode_GeneralException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(AccuWeatherResponse[].class))).thenThrow(RuntimeException.class);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accuWeatherService.getLocationByPostalCode(postalCode));

        assertTrue(exception.getMessage().contains("Could not fetch postal code for " + postalCode));
    }
}