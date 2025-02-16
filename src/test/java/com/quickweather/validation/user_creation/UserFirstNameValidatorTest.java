package com.quickweather.validation.user_creation;

import com.quickweather.dto.user.UserDto;
import com.quickweather.exceptions.UserValidationException;
import com.quickweather.validation.user.user_creation.UserFirstNameValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserFirstNameValidatorTest {

    @InjectMocks
    private UserFirstNameValidator userFirstNameValidator;

    @Test
    void validateFirstName_whenFirstNameIsNull_thenReturnException() {
        UserDto userDto = UserDto.builder()
                .firstName(null)
                .build();

        assertThrows(UserValidationException.class, () -> userFirstNameValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .build();

        assertDoesNotThrow(() -> userFirstNameValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("f")
                .build();

        assertThrows(UserValidationException.class, () -> userFirstNameValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("f".repeat(31))
                .build();

        assertThrows(UserValidationException.class, () -> userFirstNameValidator.validate(userDto));
    }
}