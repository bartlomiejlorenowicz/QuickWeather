package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.exceptions.InvalidPasswordException;
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

        assertThrows(InvalidPasswordException.class, () -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .password("abc")
                .build();

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password must be minimum 8 characters long", exception.getMessage());
    }

    @Test
    void givenPassword_whenPasswordWithoutSpecialCharacter_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .password("computer123")
                .build();

        InvalidPasswordException exception = assertThrows(InvalidPasswordException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password does not contain a special character", exception.getMessage());
    }

}