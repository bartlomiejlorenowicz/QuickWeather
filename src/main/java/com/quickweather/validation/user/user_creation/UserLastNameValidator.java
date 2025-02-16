package com.quickweather.validation.user.user_creation;

import com.quickweather.dto.user.UserDto;
import com.quickweather.exceptions.UserErrorType;
import com.quickweather.exceptions.UserValidationException;

import static java.util.Objects.isNull;

public class UserLastNameValidator extends Validator {

    @Override
    public void validate(UserDto userDto) {
        String lastName = userDto.getLastName();
        if (isNull(lastName)) {
            throw new UserValidationException(UserErrorType.INVALID_LAST_NAME, "lastname i null");
        }
        boolean lastNameTooShort = lastName.length() < 2;
        if (lastNameTooShort) {
            throw new UserValidationException(UserErrorType.INVALID_LAST_NAME, "last name must have at least 2 letters");
        }
        boolean lastNameTooLong = lastName.length() > 30;
        if (lastNameTooLong) {
            throw new UserValidationException(UserErrorType.INVALID_LAST_NAME, "last name must have maximum 30 letters");
        }
        validateNext(userDto);
    }
}
