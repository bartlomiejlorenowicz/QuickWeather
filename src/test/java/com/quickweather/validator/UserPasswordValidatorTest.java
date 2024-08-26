package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserPasswordValidatorTest {

    @InjectMocks
    private UserPasswordValidator userPasswordValidator;

    @Mock
    private Validator nextValidator;

    @Test
    void givenPassword_whenPasswordOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass123!")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertDoesNotThrow(() -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password(null)
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userPasswordValidator.validate(userDto));
    }

    @Test
    void givenPassword_whenPasswordTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("abc")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password must be minimum 8 characters long", exception.getMessage());
    }

    @Test
    void givenPassword_whenPasswordWithoutSpecialCharacter_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("computer123")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userPasswordValidator.validate(userDto));
        assertEquals("password does not contain a special character", exception.getMessage());
    }

}