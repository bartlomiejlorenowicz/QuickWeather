package com.quickweather.service.admin;

import com.quickweather.admin.SecurityEventType;
import com.quickweather.domain.SecurityEvent;
import com.quickweather.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityEventService {

    private final SecurityEventRepository repository;

    public void logEvent(String username, SecurityEventType eventType, String ipAddress) {
        SecurityEvent event = new SecurityEvent(username, eventType, ipAddress, LocalDateTime.now());
        repository.save(event);
    }

    public List<SecurityEvent> getAllEvents() {
        return repository.findAll();
    }

}
