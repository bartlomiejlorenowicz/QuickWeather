package com.quickweather.utils;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

public class UriBuilderUtils {

    public static URI buildWeatherUri(String apiUrl, String apiKey, String city) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl + "weather")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metrics")
                .build()
                .toUri();
    }

    public static URI buildZipCodeUri(String apiUrl, String apiKey, String zipcode, String countryCode) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl + "weather")
                .queryParam("zip", zipcode + "," + countryCode)
                .queryParam("appid", apiKey)
                .queryParam("lang", "pl")
                .build()
                .toUri();
    }

    public static URI buildHourlyUri(String apiUrl, String apiKey, String city) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl + "forecast")
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .queryParam("lang", "pl")
                .build()
                .toUri();
    }

    public static URI buildAirPollutionUri(String apiUrl, String apiKey, double lat, double lon) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl + "air_pollution")
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .build()
                .toUri();
    }

    public static URI buildForecastDaily(String apiUrl, String apiKey, String city, int cnt) {
        return UriComponentsBuilder.fromHttpUrl(apiUrl + "forecast/daily")
                .queryParam("q",  city)
                .queryParam("appid", apiKey)
                .queryParam("cnt", cnt)
                .build()
                .toUri();
    }

    public static URI buildPostalCodeUri(String apiUrlPostalCode, String apiKey, String postcode) {
        return UriComponentsBuilder.fromHttpUrl(apiUrlPostalCode)
                .queryParam("apikey", apiKey)
                .queryParam("q", postcode)
                .queryParam("language", "pl")
                .build()
                .toUri();
    }
}
