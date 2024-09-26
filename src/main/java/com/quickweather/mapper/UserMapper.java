package com.quickweather.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.UserDto;
import com.quickweather.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserMapper {

    private final ObjectMapper objectMapper;

    public UserMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public User toEntity(UserDto userDto) {
        log.info("starting mapping user " + userDto.getEmail());
        return objectMapper.convertValue(userDto, User.class);
    }

}
