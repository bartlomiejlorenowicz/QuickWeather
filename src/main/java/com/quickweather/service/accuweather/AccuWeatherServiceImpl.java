package com.quickweather.service.accuweather;

import com.quickweather.dto.accuweather.AccuWeatherDailyDto;
import com.quickweather.dto.accuweather.AccuWeatherDailyResponse;
import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.service.weatherbase.WeatherServiceBase;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AccuWeatherServiceImpl extends WeatherServiceBase implements AccuWeatherService {

    @Value("${accuweather.weather.api.key}")
    private String apiKey;

    @Value("${accuweather.weather.api.url}")
    private String apiUrl;

    public AccuWeatherServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<AccuWeatherResponse> getLocationByPostalCode(String postcode) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("apikey", apiKey);
        queryParams.put("q", postcode);
        queryParams.put("language", "en-us");

        URI url = UriBuilderUtils.buildUri(apiUrl, "/locations/v1/cities/search", queryParams);
        AccuWeatherResponse[] responses = fetchWeatherData(url, AccuWeatherResponse[].class, postcode);
        return Arrays.asList(responses);
    }

    @Override
    public List<AccuWeatherResponse> getLocationByCity(String city) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("apikey", apiKey);
        queryParams.put("q", city);
        queryParams.put("language", "en-us");

        URI url = UriBuilderUtils.buildUri(apiUrl, "/locations/v1/cities/search", queryParams);
        AccuWeatherResponse[] responses = fetchWeatherData(url, AccuWeatherResponse[].class, city);
        return Arrays.asList(responses);
    }

    public AccuWeatherDailyDto getLocationKeyByCity(String city) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("apikey", apiKey);
        queryParams.put("q", city);

        URI url = UriBuilderUtils.buildUri(apiUrl, "/locations/v1/cities/search", queryParams);
        log.info("Pobieranie Location Key dla miasta: {}", city);

        AccuWeatherDailyDto[] response = restTemplate.getForObject(url, AccuWeatherDailyDto[].class);
        if (response != null && response.length > 0) {
            return response[0];
        } else {
            throw new RuntimeException("Nie znaleziono Location Key dla miasta: " + city);
        }
    }

    public AccuWeatherDailyResponse getDailyForecastByLocationKey(String locationKey) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("apikey", apiKey);
        queryParams.put("language", "pl");
        queryParams.put("details", "true");

        URI url = UriBuilderUtils.buildUri(apiUrl, "/forecasts/v1/daily/1day/" + locationKey, queryParams);
        log.info("Pobieranie prognozy dziennej dla Location Key: {}", locationKey);

        return restTemplate.getForObject(url, AccuWeatherDailyResponse.class);
    }
}
