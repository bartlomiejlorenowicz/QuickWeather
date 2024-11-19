package com.quickweather.service.openweathermap;

import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.exceptions.WeatherErrorType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenWeatherServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenWeatherServiceImpl currentWeatherService;

    private static final String CITY = "London";
    private static final String ZIPCODE = "37-203";
    private static final String COUNTRY_CODE = "pl";
    private static final String TEST_API_KEY = "test-api-key";
    private static final String API_URL = "https://api.openweathermap.org/data/2.5/";

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(currentWeatherService, "apiKey", TEST_API_KEY);
        ReflectionTestUtils.setField(currentWeatherService, "apiUrl", API_URL);
    }

    @Test
    void testGetCurrentWeather() {
        WeatherResponse mockWeatherResponse = new WeatherResponse();

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherResponse.class))).thenReturn(mockWeatherResponse);

        WeatherResponse result = currentWeatherService.getCurrentWeatherByCity(CITY);

        assertEquals(mockWeatherResponse, result);
    }

    @Test
    void testGetCurrentWeatherHttpClientErrorException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> currentWeatherService.getCurrentWeatherByCity(CITY));

        assertEquals("Data not found for: " + CITY, exception.getMessage());
    }

    @Test
    void testGetCurrentWeatherGeneralException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherResponse.class)))
                .thenThrow(new RuntimeException("General exception"));

        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> {
            currentWeatherService.getCurrentWeatherByCity(CITY);
        });

        assertEquals("An unknown error occurred while fetching weather data for: " + CITY, exception.getMessage());
    }

    @Test
    void testGetCurrentWeatherByZipcodeShouldReturnCorrect() {

        WeatherByZipCodeResponseDto mockWeather = new WeatherByZipCodeResponseDto();

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherByZipCodeResponseDto.class)))
                .thenReturn(mockWeather);

        WeatherByZipCodeResponseDto result = currentWeatherService.getCurrentWeatherByZipcode(ZIPCODE, COUNTRY_CODE);

        assertEquals(mockWeather, result);
    }

    @Test
    void testGetCurrentWeatherByZipcodeGeneratedUri() {

        currentWeatherService.getCurrentWeatherByZipcode(ZIPCODE, COUNTRY_CODE);

        Mockito.verify(restTemplate).getForObject(Mockito.argThat(argument -> {
            URI uri = argument;
            return uri.toString().contains("zip=" + ZIPCODE + "," + COUNTRY_CODE)
                    && uri.toString().contains("appid=" + TEST_API_KEY)
                    && uri.toString().contains("lang=pl");
        }), Mockito.eq(WeatherByZipCodeResponseDto.class));
    }

    @Test
    void testHttpClientErrorNotFoundExceptionForZipcode() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherByZipCodeResponseDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RuntimeException exception = assertThrows(WeatherServiceException.class, () -> currentWeatherService.getCurrentWeatherByZipcode(ZIPCODE, COUNTRY_CODE));

        assertEquals("Data not found for: " + ZIPCODE + "," + COUNTRY_CODE, exception.getMessage());
    }

    @Test
    void testGeneralExceptionForZipcode() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherByZipCodeResponseDto.class)))
                .thenThrow(new WeatherServiceException(WeatherErrorType.UNKNOWN_ERROR, "General error"));

        RuntimeException exception = assertThrows(WeatherServiceException.class, () -> currentWeatherService.getCurrentWeatherByZipcode(ZIPCODE, COUNTRY_CODE));

        assertEquals("An unknown error occurred while fetching weather data for: " + ZIPCODE + "," + COUNTRY_CODE, exception.getMessage());
    }

    @Test
    void testGet5DaysForecastEvery3HoursShouldReturnCorrect() {

        HourlyForecastResponseDto mockForecast = new HourlyForecastResponseDto();

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(HourlyForecastResponseDto.class)))
                .thenReturn(mockForecast);

        HourlyForecastResponseDto result = currentWeatherService.get5DaysForecastEvery3Hours(CITY);

        assertEquals(mockForecast, result);
    }

    @Test
    void testGet5DaysForecastEvery3HoursHttpClientErrorException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(HourlyForecastResponseDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> currentWeatherService.get5DaysForecastEvery3Hours(CITY));

        assertEquals("Data not found for: " + CITY, exception.getMessage());
    }

    @Test
    void testGet5DaysForecastEvery3HoursGeneralErrorException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(HourlyForecastResponseDto.class)))
                .thenThrow(new RuntimeException("General error"));

        WeatherServiceException exception = assertThrows(WeatherServiceException.class, () -> currentWeatherService.get5DaysForecastEvery3Hours(CITY));

        assertEquals("An unknown error occurred while fetching weather data for: " + CITY, exception.getMessage());
    }
}