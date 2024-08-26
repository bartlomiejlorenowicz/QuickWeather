package com.quickweather.validator;

import com.quickweather.dto.UserDto;

import static java.util.Objects.isNull;

public class UserPasswordValidator implements Validator {

    private static final String PASSWORD_SPECIAL_CHARACTER = ".*[!@#$%^&*(),.?\\\":{}|<>].*";

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String password = userDto.getPassword();
        if (isNull(password)) {
            throw new IllegalArgumentException("password is null");
        }
        boolean passwordTooShort = password.length() < 8;
        if (passwordTooShort) {
            throw new IllegalArgumentException("password must be minimum 8 characters long");
        }
        boolean passwordDoesNotHaveSpecialCharacter = !password.matches(PASSWORD_SPECIAL_CHARACTER);
        if (passwordDoesNotHaveSpecialCharacter) {
            throw new IllegalArgumentException("password does not contain a special character");
        }
        if (nextValidator != null) {
            nextValidator.validate(userDto);
        }
    }
}
