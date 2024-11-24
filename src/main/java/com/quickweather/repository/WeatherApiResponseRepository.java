package com.quickweather.repository;

import com.quickweather.entity.ApiSource;
import com.quickweather.entity.WeatherApiResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherApiResponseRepository extends JpaRepository<WeatherApiResponse, Long> {

    Optional<WeatherApiResponse> findTopByCityAndApiSourceOrderByCreatedAtDesc(String city, ApiSource apiSource);

    List<WeatherApiResponse> findAllByCreatedAtBefore(LocalDateTime expiryTime);
}
