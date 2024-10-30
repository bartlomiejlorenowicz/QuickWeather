package com.quickweather.service.openweathermap;

import com.quickweather.dto.airpollution.AirPollutionResponseDto;
import com.quickweather.dto.forecast.HourlyForecastResponseDto;
import com.quickweather.dto.forecast.WeatherForecastDailyResponseDto;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.dto.zipcode.WeatherByZipCodeResponseDto;
import com.quickweather.service.weatherbase.WeatherServiceBase;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class OpenWeatherServiceImpl extends WeatherServiceBase implements OpenWeatherMapService {

    @Value("${open.weather.api.key}")
    private String apiKey;

    @Value("${open.weather.api.url}")
    private String apiUrl;

    public OpenWeatherServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public WeatherResponse getCurrentWeatherByCity(String city) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("q", city);
        queryParams.put("appid", apiKey);
        queryParams.put("units", "metric");

        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);
        log.info("execution request with address {}", apiUrl);
        log.info("with params {}", queryParams);

        return fetchWeatherData(url, WeatherResponse.class, city);
    }

    @Override
    public WeatherByZipCodeResponseDto getCurrentWeatherByZipcode(String zipcode, String countryCode) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("zip", zipcode + "," + countryCode);
        queryParams.put("appid", apiKey);
        queryParams.put("lang", "pl");

        URI url = UriBuilderUtils.buildUri(apiUrl, "weather", queryParams);
        return fetchWeatherData(url, WeatherByZipCodeResponseDto.class, zipcode + "," + countryCode);
    }

    @Override
    public HourlyForecastResponseDto get5DaysForecastEvery3Hours(String city) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("q", city);
        queryParams.put("appid", apiKey);
        queryParams.put("units", "metric");
        queryParams.put("lang", "pl");

        URI url = UriBuilderUtils.buildUri(apiUrl, "forecast", queryParams);
        return fetchWeatherData(url, HourlyForecastResponseDto.class, city);
    }

    @Override
    public AirPollutionResponseDto getAirPollutionByCoordinates(double lat, double lon) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("lat", String.valueOf(lat));
        queryParams.put("lon", String.valueOf(lon));
        queryParams.put("appid", apiKey);

        URI url = UriBuilderUtils.buildUri(apiUrl, "air_pollution", queryParams);
        return fetchWeatherData(url, AirPollutionResponseDto.class, lat + "," + lon);
    }

    @Override
    public WeatherForecastDailyResponseDto getWeatherForecastByCityAndDays(String city, int cnt) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("q", city);
        queryParams.put("appid", apiKey);
        queryParams.put("cnt", String.valueOf(cnt));

        URI url = UriBuilderUtils.buildUri(apiUrl, "forecast/daily", queryParams);
        return fetchWeatherData(url, WeatherForecastDailyResponseDto.class, city + "," + cnt);
    }
}
