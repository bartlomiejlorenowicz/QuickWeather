package com.quickweather.validator;

import com.quickweather.dto.UserDto;

import static java.util.Objects.isNull;

public class UserFirstNameValidator implements Validator {

    private Validator nextValidator;

    @Override
    public void setNext(Validator nextValidator) {
        this.nextValidator = nextValidator;
    }

    @Override
    public void validate(UserDto userDto) {
        String firstName = userDto.getFirstName();
        if (isNull(firstName)) {
            throw new IllegalArgumentException("first name is null");
        }
        boolean firstnameTooShort = firstName.length() < 2;
        if (firstnameTooShort) {
            throw new IllegalArgumentException("first name must have at least 2 letters");
        }
        boolean firstnameTooLong = firstName.length() > 30;
        if (firstnameTooLong) {
            throw new IllegalArgumentException("first name must have max 30 letters");
        }
        if (nextValidator != null) {
            nextValidator.validate(userDto);
        }
    }
}
