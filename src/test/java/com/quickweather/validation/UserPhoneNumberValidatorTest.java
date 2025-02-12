package com.quickweather.validation;

import com.quickweather.dto.user.UserDto;
import com.quickweather.exceptions.UserValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPhoneNumberValidatorTest {

    @InjectMocks
    private UserPhoneNumberValidator userPhoneNumberValidator;

    @Test
    void givenPhoneNumber_whenPhoneNumberIsOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .phoneNumber("2343234511")
                .build();

        assertDoesNotThrow(() -> userPhoneNumberValidator.validate(userDto));
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .phoneNumber(null)
                .build();

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number is null", exception.getMessage());
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .phoneNumber("123")
                .build();

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number must have at least 10 digits", exception.getMessage());
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .phoneNumber("1".repeat(16))
                .build();

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number must have maximum 15 digits", exception.getMessage());
    }
}