package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserCreationService {

    private final UserValidator validator;
    private final UserCreationRepository userCreationRepository;
    private final UserMapper userMapper;

    public UserCreationService(UserValidator validator, UserCreationRepository userCreationRepository, UserMapper userMapper) {
        this.validator = validator;
        this.userCreationRepository = userCreationRepository;
        this.userMapper = userMapper;
    }

    public void createUser(UserDto userDto) {
        validator.validate(userDto);
        log.info("starting saving user with mail:" + userDto.getEmail());
        userCreationRepository.save(userMapper.toEntity(userDto));
        log.info("user is saved");
    }
}
