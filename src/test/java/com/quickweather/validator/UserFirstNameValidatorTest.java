package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserFirstNameValidatorTest {

    @Mock
    private UserCreationRepository userCreationRepository;

    @InjectMocks
    private UserValidator userValidator;

    @Test
    void validateFirstName_whenFirstNameIsNull_thenReturnException() {
        UserDto userDto = UserDto.builder()
                .firstName(null)
                .lastName("lastname")
                .password("pass")
                .email("email@email.pl")
                .phoneNumber("2343234511")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertDoesNotThrow(() -> userValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("f")
                .lastName("lastname")
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("f".repeat(31))
                .lastName("lastname")
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }
}