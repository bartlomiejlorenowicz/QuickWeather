package com.quickweather.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
/**
 * Cross-Origin Resource Sharing (CORS) configuration.
 * <p>
 * This class allows requests from a frontend (Angular)
 * hosted at a different address than the backend. Here, we specifically
 * permit requests from http://localhost:4200.
 * <p>
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(allowedOrigins); // Adres frontendu
        config.addAllowedMethod("*"); // Pozwól na wszystkie metody HTTP (GET, POST, etc.)
        config.addAllowedHeader("*"); // Pozwól na wszystkie nagłówki
        config.addExposedHeader("Authorization");
        config.setAllowCredentials(true); // Pozwól na przesyłanie ciasteczek

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // Dotyczy wszystkich endpointów
        return new CorsFilter(source);
    }
}
