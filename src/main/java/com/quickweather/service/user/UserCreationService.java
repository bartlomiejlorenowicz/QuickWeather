package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.entity.*;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;

import com.quickweather.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class UserCreationService {

    private final UserValidator validator;
    private final UserRepository userCreationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserCreationService(UserValidator validator,
                               UserRepository userCreationRepository,
                               UserMapper userMapper,
                               PasswordEncoder passwordEncoder,
                               RoleRepository roleRepository) {
        this.validator = validator;
        this.userCreationRepository = userCreationRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void createUser(UserDto userDto) {
        // Walidacja danych wejściowych
        validator.validate(userDto);
        log.info("Starting saving user with email: {}", userDto.getEmail());

        // Mapowanie DTO na encję
        var userEntity = userMapper.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        // Przypisanie domyślnej roli USER
        Role defaultRole = roleRepository.findByRoleType(RoleType.USER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleType.USER, new HashSet<>())));
        // Użycie HashSet, aby kolekcja była mutowalna
        Set<Role> roles = new HashSet<>();
        roles.add(defaultRole);
        userEntity.setRoles(roles);

        // Zapis użytkownika w bazie danych
        log.info("Saving User entity to database: {}", userEntity);
        userCreationRepository.save(userEntity);
        log.info("User is saved with roles: {}", userEntity.getRoles());

    }


    public User findByEmail(String email) {
        return userCreationRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

}
