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

        // Usunięcie zbędnych spacji
        firstName = firstName.trim();

        // Sprawdzenie czy ciąg nie jest pusty po trimowaniu
        if (firstName.isEmpty()) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name cannot be empty or only whitespace");
        }

        // Walidacja długości
        if (firstName.length() < 2) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name must have at least 2 letters");
        }
        if (firstName.length() > 30) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name must have max 30 letters");
        }

        if (!firstName.matches("^[A-Za-zĄąĆćĘęŁłŃńÓóŚśŹźŻż\\-]+$")) {
            throw new UserValidationException(UserErrorType.INVALID_FIRST_NAME, "first name contains invalid characters");
        }

        validateNext(userDto);
    }
}

