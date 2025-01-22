package com.quickweather.validator;

import com.quickweather.dto.user.UserDto;
import com.quickweather.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserValidator {

    private final Validator validator;

    public UserValidator(UserRepository userCreationRepository) {
        validator = Validator.link(
                new UserEmailValidator(userCreationRepository),
                new UserFirstNameValidator(),
                new UserLastNameValidator(),
                new UserPasswordValidator()
        );
    }

    public void validate(UserDto userDto) {
        log.info("Starting validation for user with email: " + userDto.getEmail());
        validator.validate(userDto);
        log.info("Validation finished for user with email: " + userDto.getEmail());
    }
}
