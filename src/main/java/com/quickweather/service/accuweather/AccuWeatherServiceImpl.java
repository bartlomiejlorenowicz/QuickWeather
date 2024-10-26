package com.quickweather.service.accuweather;

import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.dto.weather.Main;
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

    @Value("accuweather.weather.api.url")
    private String apiUrlPostalCode;

    public AccuWeatherServiceImpl(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public List<AccuWeatherResponse> getLocationByPostalCode(String postcode) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("apikey", apiKey);
        queryParams.put("q", postcode);
        queryParams.put("language", "pl");

        URI url = UriBuilderUtils.buildUri(apiUrlPostalCode, "postcode", queryParams);
        AccuWeatherResponse[] responses = fetchWeatherData(url, AccuWeatherResponse[].class, postcode);
        return Arrays.asList(responses);
    }
}
