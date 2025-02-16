package com.quickweather.validation.user.user_creation;

import com.quickweather.dto.user.UserDto;
import com.quickweather.exceptions.UserErrorType;
import com.quickweather.exceptions.UserValidationException;

import static java.util.Objects.isNull;

public class UserFirstNameValidator extends Validator {

    @Override
    public void validate(UserDto userDto) {
        String firstName = userDto.getFirstName();
        if (isNull(firstName)) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name is null");
        }
        boolean firstnameTooShort = firstName.length() < 2;
        if (firstnameTooShort) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name must have at least 2 letters");
        }
        boolean firstnameTooLong = firstName.length() > 30;
        if (firstnameTooLong) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name must have max 30 letters");
        }
        validateNext(userDto);
    }
}
