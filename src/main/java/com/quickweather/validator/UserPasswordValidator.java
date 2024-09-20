package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.exceptions.InvalidPasswordException;

import static java.util.Objects.isNull;

public class UserPasswordValidator extends Validator {

    private static final String PASSWORD_SPECIAL_CHARACTER = ".*[!@#$%^&*(),.?\\\":{}|<>].*";

    @Override
    public void validate(UserDto userDto) {
        String password = userDto.getPassword();
        if (isNull(password)) {
            throw new InvalidPasswordException("password is null");
        }
        boolean passwordTooShort = password.length() < 8;
        if (passwordTooShort) {
            throw new InvalidPasswordException("password must be minimum 8 characters long");
        }
        boolean passwordDoesNotHaveSpecialCharacter = !password.matches(PASSWORD_SPECIAL_CHARACTER);
        if (passwordDoesNotHaveSpecialCharacter) {
            throw new InvalidPasswordException("password does not contain a special character");
        }
        validateNext(userDto);
    }
}
