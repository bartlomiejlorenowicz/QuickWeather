package com.quickweather.service.accuweather;

import com.quickweather.dto.accuweather.AccuWeatherResponse;
import com.quickweather.utils.UriBuilderUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AccuWeatherServiceImpl implements AccuWeatherService {

    @Value("${accuweather.weather.api.key}")
    private String apiKey;

    @Value("accuweather.weather.api.url")
    private String apiUrlPostalCode;

    private final RestTemplate restTemplate;

    public AccuWeatherServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<AccuWeatherResponse> getLocationByPostalCode(String postcode) {
        URI url = UriBuilderUtils.buildPostalCodeUri(apiUrlPostalCode, apiKey, postcode);

        try {
            AccuWeatherResponse[] responseArray = restTemplate.getForObject(url, AccuWeatherResponse[].class);
            return Arrays.asList(responseArray);
        } catch (HttpClientErrorException e) {
            log.error("HTTP error fetching postal code for {}: {}", postcode, e.getMessage());
            throw new RuntimeException("Error fetching postal code: " + postcode);
        } catch (Exception e) {
            log.error("HTTP Error fetching postal code for {}: {}", postcode, e.getMessage());
            throw new RuntimeException("Could not fetch postal code for " + postcode, e);
        }
    }
}
