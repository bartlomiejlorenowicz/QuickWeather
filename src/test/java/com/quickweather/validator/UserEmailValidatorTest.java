package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserEmailValidatorTest {

    @Mock
    private UserCreationRepository userCreationRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void givenEmail_WhenEmailOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass123!")
                .email("first@wp.pl")
                .phoneNumber("1234567891")
                .build();

        assertDoesNotThrow(() -> userValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailNotOk_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass123!")
                .email("first.pl")
                .phoneNumber("1234567891")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass123!")
                .email(null)
                .phoneNumber("1234567891")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailExist_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass123!")
                .email("first@wp.pl")
                .phoneNumber("1234567891")
                .build();
        when(userCreationRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }
}