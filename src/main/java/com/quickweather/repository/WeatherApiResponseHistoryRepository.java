package com.quickweather.repository;

import com.quickweather.entity.ApiSource;
import com.quickweather.entity.WeatherApiResponseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface WeatherApiResponseHistoryRepository extends JpaRepository<WeatherApiResponseHistory, Long> {
    Optional<WeatherApiResponseHistory> findFirstByCityAndApiSourceOrderByArchivedAtDesc(String city, ApiSource apiSource);
}
