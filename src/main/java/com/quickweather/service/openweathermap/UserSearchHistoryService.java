package com.quickweather.service.openweathermap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.weather.UserSearchHistoryResponse;
import com.quickweather.dto.weather.WeatherResponse;
import com.quickweather.entity.ApiSource;
import com.quickweather.entity.User;
import com.quickweather.entity.UserSearchHistory;
import com.quickweather.entity.WeatherApiResponse;
import com.quickweather.exceptions.UserNotFoundException;
import com.quickweather.repository.UserRepository;
import com.quickweather.repository.UserSearchHistoryRepository;
import com.quickweather.repository.WeatherApiResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserSearchHistoryService {

    private final UserSearchHistoryRepository userSearchHistoryRepository;
    private final UserRepository userRepository;
    private final WeatherApiResponseRepository weatherApiResponseRepository;
    private final ObjectMapper objectMapper;

    public List<UserSearchHistoryResponse> getUserSearchHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        // Pobierz historię użytkownika z bazy danych
        Page<UserSearchHistory> history = userSearchHistoryRepository.findByUserId(userId, pageable);

        // Mapuj na `UserSearchHistoryResponse`
        return history.stream().map(h -> {
            UserSearchHistoryResponse response = new UserSearchHistoryResponse();
            response.setCity(h.getCity());
            response.setSearchedAt(h.getSearchedAt());

            // Deserializacja JSON na `WeatherResponse`
            try {
                WeatherResponse weatherResponse = objectMapper.treeToValue(h.getWeatherApiResponse().getResponseJson(), WeatherResponse.class);
                response.setWeather(weatherResponse);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize weather response JSON", e);
            }

            return response;
        }).toList();
    }

    public void saveSearchHistory(Long userId, String city, WeatherResponse weatherResponse) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Sprawdzamy, czy odpowiedź pogodowa już istnieje w bazie
        WeatherApiResponse existingWeatherApiResponse = weatherApiResponseRepository.findByCityAndApiSource(city, ApiSource.OPEN_WEATHER);

        // Jeśli nie istnieje, zapisujemy ją w WeatherApiResponse
        if (existingWeatherApiResponse == null) {
            WeatherApiResponse weatherApiResponse = new WeatherApiResponse();
            weatherApiResponse.setCity(city);
            weatherApiResponse.setApiSource(ApiSource.OPEN_WEATHER);
            weatherApiResponse.setResponseJson(objectMapper.valueToTree(weatherResponse));
            weatherApiResponse.setRequestJson(objectMapper.valueToTree(weatherResponse));
            weatherApiResponse.setCreatedAt(LocalDateTime.now());

            existingWeatherApiResponse = weatherApiResponseRepository.save(weatherApiResponse);
        }

        // Tworzymy i zapisujemy historię wyszukiwań użytkownika
        UserSearchHistory searchHistory = new UserSearchHistory();
        searchHistory.setUser(user);
        searchHistory.setCity(city);
        searchHistory.setWeatherApiResponse(existingWeatherApiResponse); // Powiązanie z istniejącą odpowiedzią pogodową
        searchHistory.setSearchedAt(LocalDateTime.now());

        userSearchHistoryRepository.save(searchHistory);
    }

}
