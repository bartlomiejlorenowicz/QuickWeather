package com.quickweather.service.user;

import com.quickweather.dto.user.UserId;
import com.quickweather.domain.User;
import com.quickweather.exceptions.UserErrorType;
import com.quickweather.exceptions.UserValidationException;
import com.quickweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserStatusService {

    private final UserRepository userRepository;
    private final UserCrudService userCrudService;

    public UserStatusService(UserRepository userCreationRepository, UserCrudService userCrudService) {
        this.userRepository = userCreationRepository;
        this.userCrudService = userCrudService;
    }

    @Transactional
    public void enableUser(UserId userId) {
        User user = userCrudService.findById(userId.getValue());
        if (user.isEnabled()) {
            throw new UserValidationException(UserErrorType.ACCOUNT_ENABLED, "User is already enabled");
        }
        user.setEnabled(true);
        userRepository.save(user);
        log.info("User with ID {} has been enabled", userId);
    }

    @Transactional
    public void disableUser(UserId userId) {
        User user = userCrudService.findById(userId.getValue());
        if (!user.isEnabled()) {
            throw new UserValidationException(UserErrorType.ACCOUNT_DISABLED, "User is already disabled");
        }
        user.setEnabled(false);
        userRepository.save(user);
        log.info("User with ID {} has been disabled", userId);
    }

}
