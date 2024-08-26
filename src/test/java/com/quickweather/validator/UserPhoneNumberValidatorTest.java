package com.quickweather.validator;

import com.quickweather.dto.UserDto;
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
                .firstName("firstname")
                .lastName("lastname")
                .password("P@sword!")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertDoesNotThrow(() -> userPhoneNumberValidator.validate(userDto));
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("P@sword!")
                .email("valid.email@example.com")
                .phoneNumber(null)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number is null", exception.getMessage());
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("P@sword!")
                .email("valid.email@example.com")
                .phoneNumber("123")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number must have at least 10 digits", exception.getMessage());
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("P@sword!")
                .email("valid.email@example.com")
                .phoneNumber("1".repeat(16))
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number must have maximum 15 digits", exception.getMessage());
    }
}