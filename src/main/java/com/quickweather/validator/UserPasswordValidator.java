package com.quickweather.validator;

import com.quickweather.dto.UserDto;

public class UserPasswordValidator implements Validator {

    private static final String PASSWORD_REGEX = ".*[!@#$%^&*(),.?\\\":{}|<>].*";

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String password = userDto.getPassword();
        if (password == null) {
            throw new IllegalArgumentException("password is null");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("password must be minimum 8 characters long");
        }
        if (!password.matches(PASSWORD_REGEX)) {
            throw new IllegalArgumentException("password does not contain a special character");
        }
        if (nextValidator != null) {
            nextValidator.validate(userDto);
        }
    }
}
