package com.quickweather.validation;

import com.quickweather.dto.user.UserDto;
import com.quickweather.exceptions.UserValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPasswordValidatorTest {

    @InjectMocks
    private UserPasswordValidator userPasswordValidator;

    @Test
    void givenPassword_whenPasswordOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .password("pass123!")
                .build();

        assertDoesNotThrow(() -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .password(null)
                .build();

        assertThrows(UserValidationException.class, () -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .password("abc")
                .build();

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password must be minimum 8 characters long", exception.getMessage());
    }

    @Test
    void givenPassword_whenPasswordWithoutSpecialCharacter_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .password("computer123")
                .build();

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password does not contain a special character", exception.getMessage());
    }

}