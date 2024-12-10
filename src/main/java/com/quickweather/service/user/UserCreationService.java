package com.quickweather.service.user;

import com.quickweather.dto.user.Role;
import com.quickweather.dto.user.UserDto;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class UserCreationService {

    private final UserValidator validator;
    private final UserCreationRepository userCreationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserCreationService(UserValidator validator, UserCreationRepository userCreationRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.userCreationRepository = userCreationRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public void createUser(UserDto userDto) {
        validator.validate(userDto);
        log.info("starting saving user with mail:" + userDto.getEmail());

        var userEntity = userMapper.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        if (userDto.getRole() == null) {
            userEntity.setRole(Role.USER);
        } else {
            userEntity.setRole(userDto.getRole());
        }

        log.info("Saving User entity to database: {}", userEntity);
        userCreationRepository.save(userEntity);
        log.info("User is saved with roles: {}", userEntity.getRole());
    }
}
