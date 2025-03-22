package com.quickweather.service.user;

import com.quickweather.admin.SecurityEventType;
import com.quickweather.domain.User;
import com.quickweather.repository.UserRepository;
import com.quickweather.service.admin.SecurityEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLoginAttemptService {

    private final UserRepository userRepository;
    private final SecurityEventService securityEventService;

    public void incrementFailedAttempts(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= 5) {
                user.setEnabled(false);
                user.setLockUntil(LocalDateTime.now().plusMinutes(15));
                log.info("user exceeded login attempts");
                securityEventService.logEvent(email, SecurityEventType.MULTIPLE_LOGIN_ATTEMPTS, "system");
            }
            userRepository.save(user);
            log.info("Incrementing failed attempts for user {}, current attempts={}", email, user.getFailedAttempts());
        }
    }

    public void resetFailedAttempts(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFailedAttempts(0);
            user.setEnabled(true);
            user.setUpdatedAt(null);
            userRepository.save(user);
        }
    }

    public boolean isAccountLocked(User user) {
        if (!user.isEnabled() && user.getLockUntil() != null) {
            if (user.getLockUntil().isBefore(LocalDateTime.now())) {
                user.setEnabled(true);
                user.setFailedAttempts(0);
                user.setLockUntil(null);
                userRepository.save(user);
                return false;
            }
            return true;
        }
        return false;
    }

}
