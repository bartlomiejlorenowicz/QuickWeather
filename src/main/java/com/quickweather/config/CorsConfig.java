package com.quickweather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:4200"); // Adres frontendu
        config.addAllowedMethod("*"); // Pozwól na wszystkie metody HTTP (GET, POST, etc.)
        config.addAllowedHeader("*"); // Pozwól na wszystkie nagłówki
        config.addExposedHeader("Authorization");
        config.setAllowCredentials(true); // Pozwól na przesyłanie ciasteczek

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Dotyczy wszystkich endpointów
        return new CorsFilter(source);
    }
}
