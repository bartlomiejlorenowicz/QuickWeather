package com.quickweather.service.user;

import com.quickweather.domain.Role;
import com.quickweather.domain.RoleType;
import com.quickweather.domain.User;
import com.quickweather.dto.user.UserDto;
import com.quickweather.mapper.UserMapper;

import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;
import com.quickweather.validation.user.user_creation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

/**
 * Coordinates the entire user creation process, including:
 * <ul>
 *   <li>Validating input data via {@link UserValidator}</li>
 *   <li>Mapping a {@link UserDto} to a User entity via {@link UserMapper}</li>
 *   <li>Encoding the user's password with {@link PasswordEncoder}</li>
 *   <li>Assigning default roles using {@link UserRoleService}</li>
 *   <li>Saving the new user to the database via {@link UserSearchService}</li>
 *   <li>Sending a welcome email via {@link UserNotificationService}</li>
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserCreationService {

    private final UserValidator validator;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final UserNotificationService userNotificationService;
    private final RoleRepository roleRepository;


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
        userRepository.save(userEntity);
        log.info("User is saved with roles: {}", userEntity.getRoles());

        // Send welcome email
        userNotificationService.sendWelcomeEmail(userDto.getEmail(), userDto.getFirstName());
    }

    public User createOrUpdateUser(String firstName, String lastName, String email, String rawPassword, RoleType primaryRole) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        // Pobieramy obie role: ADMIN oraz USER
        Role adminRole = roleRepository.findByRoleType(RoleType.ADMIN)
                .orElseThrow(() -> new RuntimeException("Rola " + RoleType.ADMIN + " nie została znaleziona"));
        Role userRole = roleRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new RuntimeException("Rola " + RoleType.USER + " nie została znaleziona"));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("Przed aktualizacją, role użytkownika {}: {}", email, user.getRoles());
            user.getRoles().clear();
            // obie role
            user.getRoles().add(adminRole);
            user.getRoles().add(userRole);
            user.setPassword(passwordEncoder.encode(rawPassword));
            User updatedUser = userRepository.save(user);
            log.info("Po aktualizacji, role użytkownika {}: {}", email, updatedUser.getRoles());
            return updatedUser;
        } else {
            User user = new User();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setUuid(UUID.randomUUID());
            user.setEnabled(true);
            // Przypisujemy obie role przy tworzeniu nowego konta admina
            user.getRoles().add(adminRole);
            user.getRoles().add(userRole);
            return userRepository.save(user);
        }
    }




}
