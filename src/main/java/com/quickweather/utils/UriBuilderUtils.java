package com.quickweather.utils;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

public class UriBuilderUtils {

    public static URI buildUri(String url, String endpoint, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url + endpoint);
        queryParams.forEach(builder::queryParam);

        return builder.build().toUri();
    }
}
