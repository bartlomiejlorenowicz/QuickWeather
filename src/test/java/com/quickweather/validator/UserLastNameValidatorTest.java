package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class UserLastNameValidatorTest {

    private UserCreationRepository userCreationRepository;
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userCreationRepository = Mockito.mock(UserCreationRepository.class);
        userValidator = new UserValidator(userCreationRepository);
    }

    @Test
    void validLastName_whenLastNameIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName(null)
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("l")
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("l".repeat(31))
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .lastName("lastname")
                .password("pass")
                .email("valid.email@example.com")
                .phoneNumber("2343234511")
                .build();

        assertDoesNotThrow(() -> userValidator.validate(userDto));
    }
}