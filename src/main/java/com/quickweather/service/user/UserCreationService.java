package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.mapper.UserMapper;

import com.quickweather.validation.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

/**
 * Coordinates the entire user creation process, including:
 * <ul>
 *   <li>Validating input data via {@link UserValidator}</li>
 *   <li>Mapping a {@link UserDto} to a User entity via {@link UserMapper}</li>
 *   <li>Encoding the user's password with {@link PasswordEncoder}</li>
 *   <li>Assigning default roles using {@link UserRoleService}</li>
 *   <li>Saving the new user to the database via {@link UserCrudService}</li>
 *   <li>Sending a welcome email via {@link UserNotificationService}</li>
 * </ul>
 */
@Service
@Slf4j
public class UserCreationService {

    private final UserValidator validator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserCrudService userCrudService;
    private final UserRoleService userRoleService;
    private final UserNotificationService userNotificationService;

    public UserCreationService(UserValidator validator,
                               UserMapper userMapper,
                               PasswordEncoder passwordEncoder,
                               UserCrudService userCrudService,
                               UserRoleService userRoleService,
                               UserNotificationService userNotificationService) {
        this.validator = validator;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userCrudService = userCrudService;
        this.userRoleService = userRoleService;
        this.userNotificationService = userNotificationService;
    }

    /**
     * Validates and creates a new user, assigns a default role,
     * saves to the database, and sends a welcome email.
     *
     * @param userDto the data for the new user
     */
    public void createUser(UserDto userDto) {
        // Validate input data
        validator.validate(userDto);
        log.info("Starting saving user with email: {}", userDto.getEmail());

        // Map DTO to entity & encode password
        var userEntity = userMapper.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Assign default role(s)
        userEntity.setRoles(new HashSet<>());
        userRoleService.assignDefaultUserRole(userEntity.getRoles());

        // Save user in database
        log.info("Saving User entity to database: {}", userEntity);
        userCrudService.save(userEntity);
        log.info("User is saved with roles: {}", userEntity.getRoles());

        // Send welcome email
        userNotificationService.sendWelcomeEmail(userDto.getEmail(), userDto.getFirstName());
    }

}
