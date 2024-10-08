package com.quickweather.service.user;

import com.quickweather.dto.user.UserId;
import com.quickweather.entity.User;
import com.quickweather.exceptions.UserErrorType;
import com.quickweather.exceptions.UserValidationException;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserStatusService {

    private final UserCreationRepository userCreationRepository;

    public UserStatusService(UserCreationRepository userCreationRepository) {
        this.userCreationRepository = userCreationRepository;
    }

    @Transactional
    public void enableUser(UserId userId) {
        User user = findUserById(userId.getValue());
        if (user.isEnabled()) {
            throw new UserValidationException(UserErrorType.ACCOUNT_ENABLED, "User is already enabled");
        }
        user.setEnabled(true);
        userCreationRepository.save(user);
        log.info("User with ID {} has been enabled", userId);
    }

    @Transactional
    public void disabledUser(UserId userId) {
        User user = findUserById(userId.getValue());
        if (!user.isEnabled()) {
            throw new UserValidationException(UserErrorType.ACCOUNT_DISABLED, "User is already disabled");
        }
        user.setEnabled(false);
        userCreationRepository.save(user);
        log.info("User with ID {} has been disabled", userId);
    }

    private User findUserById(Long userId) {
        return userCreationRepository.findById(userId)
                .orElseThrow(() -> new UserValidationException(UserErrorType.INVALID_EMAIL, "User not found"));
    }

}
