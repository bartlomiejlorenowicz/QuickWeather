package com.quickweather.repository;

import com.quickweather.entity.ApiSource;
import com.quickweather.entity.WeatherApiResponseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WeatherApiResponseHistoryRepository extends JpaRepository<WeatherApiResponseHistory, Long> {
    Optional<WeatherApiResponseHistory> findFirstByCityAndApiSourceOrderByArchivedAtDesc(String city, ApiSource apiSource);
}
