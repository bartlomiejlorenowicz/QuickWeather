package com.quickweather.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "weather_api_responses")
public class WeatherApiResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    @Column(name = "country_code", length = 2)
    private String countryCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiSource apiSource;

    @Type(JsonBinaryType.class)
    @Column(name = "response_json", columnDefinition = "json")
    private Object responseJson;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
