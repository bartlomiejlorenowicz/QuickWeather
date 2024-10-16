package com.quickweather.service.openweathermap;

import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@Service
public class OpenWeatherServiceImpl implements OpenWeatherMapService {

    @Value("${open.weather.api.key}")
    private String apiKey;

    @Value("${open.weather.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public OpenWeatherServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public WeatherResponse getCurrentWeatherByCity(String city) {
        URI url = UriBuilderUtils.buildWeatherUri(apiUrl, apiKey, city);

        try {
            return restTemplate.getForObject(url, WeatherResponse.class);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching weather data for {}: {}", city, e.getMessage());
            throw new RuntimeException("Error fetching weather data for " + city);
        } catch (Exception e) {
            log.error("HTTP error fetching weather data for {}: {}", city, e.getMessage());
            throw new RuntimeException("Could not fetch weather data for " + city);
        }
    }

    @Override
    public WeatherByZipCodeResponseDto getCurrentWeatherByZipcode(String zipcode, String countryCode) {
        URI url = UriBuilderUtils.buildZipCodeUri(apiUrl, apiKey, zipcode, countryCode);

        try {
            return restTemplate.getForObject(url, WeatherByZipCodeResponseDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching country by zipcode {}", zipcode);
            throw new RuntimeException("Error fetching country by zipcode " + zipcode);
        } catch (Exception e) {
            log.error("HTTP error fetching country by zipcode " + zipcode);
            throw new RuntimeException("Could not fetch country by zipcode " + zipcode);
        }
    }

    @Override
    public HourlyForecastResponseDto get5DaysForecastEvery3Hours(String city) {
        URI url = UriBuilderUtils.buildHourlyUri(apiUrl, apiKey, city);

        try {
            return restTemplate.getForObject(url, HourlyForecastResponseDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching forecast for city {}", city);
            throw new RuntimeException("Error fetching forecast for city: " + city);
        } catch (Exception e) {
            log.error("HTTP error fetching forecast for city: " + city);
            throw new RuntimeException("Could not fetch forecast for city " + city);
        }
    }

    @Override
    public AirPollutionResponseDto getAirPollutionByCoordinates(double lat, double lon) {
        URI url = UriBuilderUtils.buildAirPollutionUri(apiUrl, apiKey, lat, lon);

        try {
            return restTemplate.getForObject(url, AirPollutionResponseDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching air pollution for lat and lon {}, {}", lat, lon);
            throw new RuntimeException("Error fetching air pollution for lat and lon", e);
        } catch (Exception e) {
            log.error("HTTP error fetching for air pollution lat {}, lon {}", lat, lon);
            throw new RuntimeException("Could not fetch air pollution for lat " + lat + " and lon " + lon, e);
        }
    }

    @Override
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(String city, int cnt) {
        URI url = UriBuilderUtils.buildForecastDaily(apiUrl, apiKey, city, cnt);

        try {
            return restTemplate.getForObject(url, WeatherForecastDailyResponseDto.class);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching daily forecast for city {}, cnt {}", city, cnt);
            throw new RuntimeException("error fetching daily forecast for city " + city + " cnt " + cnt);
        } catch (Exception e) {
            log.error("HTTP error fetching daily forecast for city {}, cnt {}", city, cnt);
            throw new RuntimeException("Could not fetch daily forecast for city " + city + " cnt " + cnt, e);
        }
    }
}
