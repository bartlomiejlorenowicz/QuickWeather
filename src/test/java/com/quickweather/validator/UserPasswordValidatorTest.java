package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserPasswordValidatorTest {

    private UserPasswordValidator userPasswordValidator;

    @BeforeEach
    void setup() {
        userPasswordValidator = new UserPasswordValidator();
    }

    @Test
    void givenPassword_whenPasswordOk_thenDoesNotThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "pass123!", "email@wp.pl", "2343235556");

        assertDoesNotThrow(() -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordIsNull_thenThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", null, "email@wp.pl", "2343235556");

        assertThrows(IllegalArgumentException.class, () -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordTooShort_thenThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "abc", "email@wp.pl", "2343235556");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password must be minimum 8 characters long", exception.getMessage());
    }

    @Test
    void givenPassword_whenPasswordWithoutSpecialCharacter_thenThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "computer123", "email@wp.pl", "2343235556");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password does not contain a special character", exception.getMessage());
    }

}