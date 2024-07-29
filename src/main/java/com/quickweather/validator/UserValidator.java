package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserValidator {

    public void validate(UserDto userDto) {
        log.info("Starting validation user with mail: " + userDto.getEmail() );
    }
}
