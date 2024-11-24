package com.quickweather.schedule;

import com.quickweather.entity.WeatherApiResponse;
import com.quickweather.entity.WeatherApiResponseHistory;
import com.quickweather.repository.WeatherApiResponseHistoryRepository;
import com.quickweather.repository.WeatherApiResponseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class WeatherCleanupService {
    private final WeatherApiResponseRepository weatherApiResponseRepository;
    private final WeatherApiResponseHistoryRepository weatherApiResponseHistoryRepository;

    public WeatherCleanupService(WeatherApiResponseRepository weatherApiResponseRepository,
                                 WeatherApiResponseHistoryRepository weatherApiResponseHistoryRepository) {
        this.weatherApiResponseRepository = weatherApiResponseRepository;
        this.weatherApiResponseHistoryRepository = weatherApiResponseHistoryRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 86400000)
    public void archiveOldWeatherData() {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(5);

        // dane do archiwizacji
        List<WeatherApiResponse> oldData = weatherApiResponseRepository.findAllByCreatedAtBefore(expiryTime);

        if (!oldData.isEmpty()) {
            log.info("Found {} records to archive.", oldData.size());
            List<WeatherApiResponseHistory> historyData = oldData.stream().map(record -> {
                WeatherApiResponseHistory history = new WeatherApiResponseHistory();
                history.setCity(record.getCity());
                history.setCountryCode(record.getCountryCode());
                history.setApiSource(record.getApiSource());
                history.setResponseJson(record.getResponseJson());
                history.setCreatedAt(record.getCreatedAt());
                history.setArchivedAt(LocalDateTime.now());
                return history;
            }).toList();

            // Zapisanie do tabeli historycznej
            weatherApiResponseHistoryRepository.saveAll(historyData);

            // Usuwanie z głównej tabeli
            weatherApiResponseRepository.deleteAll(oldData);
        }
    }
}
