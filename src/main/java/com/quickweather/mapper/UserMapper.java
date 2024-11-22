package com.quickweather.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.user.UserDto;
import com.quickweather.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserMapper {

    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public User toEntity(UserDto userDto) {
        log.info("starting mapping user " + userDto.getEmail());
        User user = objectMapper.convertValue(userDto, User.class);
        user.setUuid(UUID.randomUUID());
        return user;
    }

}
