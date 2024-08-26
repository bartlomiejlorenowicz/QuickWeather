package com.quickweather.validator;

import com.quickweather.dto.UserDto;

import static java.util.Objects.isNull;

public class UserLastNameValidator extends AbstractValidator {

    @Override
    protected void doValidate(UserDto userDto) {
        String lastName = userDto.getLastName();
        if (isNull(lastName)) {
            throw new IllegalArgumentException("lastname i null");
        }
        boolean lastNameTooShort = lastName.length() < 2;
        if (lastNameTooShort) {
            throw new IllegalArgumentException("last name must have at least 2 letters");
        }
        boolean lastNameTooLong = lastName.length() > 30;
        if (lastNameTooLong) {
            throw new IllegalArgumentException("last name must have maximum 30 letters");
        }
    }
}
