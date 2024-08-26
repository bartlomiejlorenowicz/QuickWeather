package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UserValidator {

    private final Validator validatorChain;

    public UserValidator(UserCreationRepository userCreationRepository) {
        UserFirstNameValidator firstNameValidator = new UserFirstNameValidator();
        UserLastNameValidator lastNameValidator = new UserLastNameValidator();
        UserEmailValidator emailValidator = new UserEmailValidator(userCreationRepository);
        UserPasswordValidator passwordValidator = new UserPasswordValidator();
        UserPhoneNumberValidator phoneNumberValidator = new UserPhoneNumberValidator();

        this.validatorChain = ValidatorChainBuilder.buildChain(
                Arrays.asList(firstNameValidator, lastNameValidator, emailValidator, passwordValidator, phoneNumberValidator)
        );
    }

    public void validate(UserDto userDto) {
        log.info("Starting validation for user with email: " + userDto.getEmail());
        validatorChain.validate(userDto);
        log.info("Validation finished for user with email: " + userDto.getEmail());
    }
}
