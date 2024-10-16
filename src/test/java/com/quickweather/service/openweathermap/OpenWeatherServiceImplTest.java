package com.quickweather.service.openweathermap;

import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
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

    private final String city = "London";

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        currentWeatherService = new OpenWeatherServiceImpl(restTemplate);
        Field apiKey = OpenWeatherServiceImpl.class.getDeclaredField("apiKey");
        apiKey.setAccessible(true);
        apiKey.set(currentWeatherService, "test-api-key");

        Field apiUrl = OpenWeatherServiceImpl.class.getDeclaredField("apiUrl");
        apiUrl.setAccessible(true);
        apiUrl.set(currentWeatherService, "https://api.openweathermap.org/data/2.5/");
    }

    @Test
    void testGetCurrentWeather() {
        WeatherResponse mockWeatherResponse = new WeatherResponse();

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherResponse.class))).thenReturn(mockWeatherResponse);

        WeatherResponse result = currentWeatherService.getCurrentWeatherByCity(city);

        assertEquals(mockWeatherResponse, result);
    }

    @Test
    void testGetCurrentWeatherHttpClientErrorException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currentWeatherService.getCurrentWeatherByCity(city);
        });

        assertEquals("Error fetching weather data for " + city, exception.getMessage());
    }

    @Test
    void testGetCurrentWeatherGeneralException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherResponse.class)))
                .thenThrow(new RuntimeException("General exception"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currentWeatherService.getCurrentWeatherByCity(city);
        });

        assertEquals("Could not fetch weather data for " + city, exception.getMessage());
    }

    @Test
    void testGetCurrentWeatherByZipcodeShouldReturnCorrect() {

        String zipcode = "37-203";
        String countryCode = "pl";
        WeatherByZipCodeResponseDto mockWeather = new WeatherByZipCodeResponseDto();

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherByZipCodeResponseDto.class)))
                .thenReturn(mockWeather);

        WeatherByZipCodeResponseDto result = currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode);

        assertEquals(mockWeather, result);
    }

    @Test
    void testGetCurrentWeatherByZipcodeGeneratedUri() {

        String zipcode = "37-203";
        String countryCode = "pl";

        currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode);

        Mockito.verify(restTemplate).getForObject(Mockito.argThat(argument -> {
            URI uri = (URI) argument;
            return uri.toString().contains("zip=37-203,pl")
                    && uri.toString().contains("appid=test-api-key")
                    && uri.toString().contains("lang=pl");
        }), Mockito.eq(WeatherByZipCodeResponseDto.class));
    }

    @Test
    void testHttpClientErrorExceptionForZipcode() {

        String zipcode = "37-203";
        String countryCode = "pl";

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherByZipCodeResponseDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode));

        assertEquals("Error fetching country by zipcode " + zipcode, exception.getMessage());
    }

    @Test
    void testGeneralExceptionForZipcode() {

        String zipcode = "37-203";
        String countryCode = "pl";

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(WeatherByZipCodeResponseDto.class)))
                .thenThrow(new RuntimeException("General error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> currentWeatherService.getCurrentWeatherByZipcode(zipcode, countryCode));

        assertEquals("Could not fetch country by zipcode " + zipcode, exception.getMessage());
    }

    @Test
    void testGet5DaysForecastEvery3HoursShouldReturnCorrect() {

        HourlyForecastResponseDto mockForecast = new HourlyForecastResponseDto();

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(HourlyForecastResponseDto.class)))
                .thenReturn(mockForecast);

        HourlyForecastResponseDto result = currentWeatherService.get5DaysForecastEvery3Hours(city);

        assertEquals(mockForecast, result);
    }

    @Test
    void testGet5DaysForecastEvery3HoursHttpClientErrorException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(HourlyForecastResponseDto.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> currentWeatherService.get5DaysForecastEvery3Hours(city));

        assertEquals("Error fetching forecast for city: " + city, exception.getMessage());
    }

    @Test
    void testGet5DaysForecastEvery3HoursGeneralErrorException() {

        when(restTemplate.getForObject(any(URI.class), Mockito.eq(HourlyForecastResponseDto.class)))
                .thenThrow(new RuntimeException("General error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> currentWeatherService.get5DaysForecastEvery3Hours(city));

        assertEquals("Could not fetch forecast for city " + city, exception.getMessage());
    }
}