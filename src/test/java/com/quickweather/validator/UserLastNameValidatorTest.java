package com.quickweather.validator;

import com.quickweather.dto.user.UserDto;
import com.quickweather.exceptions.UserValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class UserLastNameValidatorTest {

    @InjectMocks
    private UserLastNameValidator userLastNameValidator;

    @Test
    void validLastName_whenLastNameIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .lastName(null)
                .build();

        Assertions.assertThrows(UserValidationException.class, () -> userLastNameValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .lastName("l")
                .build();

        Assertions.assertThrows(UserValidationException.class, () -> userLastNameValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .lastName("l".repeat(31))
                .build();

        Assertions.assertThrows(UserValidationException.class, () -> userLastNameValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .lastName("lastname")
                .build();

        assertDoesNotThrow(() -> userLastNameValidator.validate(userDto));
    }
}