package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.exceptions.InvalidLastNameException;

import static java.util.Objects.isNull;

public class UserLastNameValidator extends Validator {

    @Override
    public void validate(UserDto userDto) {
        String lastName = userDto.getLastName();
        if (isNull(lastName)) {
            throw new InvalidLastNameException("lastname i null");
        }
        boolean lastNameTooShort = lastName.length() < 2;
        if (lastNameTooShort) {
            throw new InvalidLastNameException("last name must have at least 2 letters");
        }
        boolean lastNameTooLong = lastName.length() > 30;
        if (lastNameTooLong) {
            throw new InvalidLastNameException("last name must have maximum 30 letters");
        }
        validateNext(userDto);
    }
}
