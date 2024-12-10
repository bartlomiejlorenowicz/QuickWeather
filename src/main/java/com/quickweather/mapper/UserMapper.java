package com.quickweather.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.user.Role;
import com.quickweather.dto.user.UserDto;
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
        log.info("Mapping UserDto to User entity for email: {}", userDto.getEmail());
        return User.builder()
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .phoneNumber(userDto.getPhoneNumber())
                .isEnabled(true)
                .isLocked(userDto.isLocked())
                .role(userDto.getRole() == null ? Role.USER : userDto.getRole())
                .build();
    }

    public UserDto toDto(User user) {
        log.info("Mapping User entity to UserDto for email: {}", user.getEmail());
        return UserDto.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .isEnabled(user.isEnabled())
                .isLocked(user.isLocked())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
