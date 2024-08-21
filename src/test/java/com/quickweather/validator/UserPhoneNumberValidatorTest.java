package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserPhoneNumberValidatorTest {

    private UserPhoneNumberValidator userPhoneNumberValidator;

    @BeforeEach
    void setUp() {
        userPhoneNumberValidator = new UserPhoneNumberValidator();
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberIsOk_thenDoesNotThrowException() {
        UserDto userDto = new UserDto("first", "last", "P@sword!", "abc@wp.pl", "1234556677");

        assertDoesNotThrow(() -> userPhoneNumberValidator.validate(userDto));
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberIsNull_thenThrowException() {
        UserDto userDto = new UserDto("first", "last", "P@sword!", "abc@wp.pl", null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number is null", exception.getMessage());
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberTooShort_thenThrowException() {
        UserDto userDto = new UserDto("first", "last", "P@sword!", "abc@wp.pl", "123341111");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number must be between 10 and 15 digits", exception.getMessage());
    }

    @Test
    void givenPhoneNumber_whenPhoneNumberTooLong_thenThrowException() {
        UserDto userDto = new UserDto("first", "last", "P@sword!", "abc@wp.pl", "9".repeat(16));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPhoneNumberValidator.validate(userDto));
        assertEquals("phone number must be between 10 and 15 digits", exception.getMessage());
    }


}