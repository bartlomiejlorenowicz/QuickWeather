package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Slf4j
public class UserValidator {

    private final UserCreationRepository userCreationRepository;

    public UserValidator(UserCreationRepository userCreationRepository) {
        this.userCreationRepository = userCreationRepository;
    }

    public void validate(UserDto userDto) {
        log.info("Starting validation user with email: " + userDto.getEmail());

        UserFirstNameValidator userFirstNameValidator = new UserFirstNameValidator();
        UserLastNameValidator userLastNameValidator = new UserLastNameValidator();
        UserEmailValidator userEmailValidator = new UserEmailValidator();
        UserPasswordValidator userPasswordValidator = new UserPasswordValidator();
        UserPhoneNumberValidator userPhoneNumberValidator = new UserPhoneNumberValidator();

        userFirstNameValidator.setNext(userLastNameValidator);
        userLastNameValidator.setNext(userEmailValidator);
        userEmailValidator.setNext(userPasswordValidator);
        userPasswordValidator.setNext(userPhoneNumberValidator);

        userFirstNameValidator.validate(userDto);

        log.info("Ending validation user with email" + userDto.getEmail());
    }
}
