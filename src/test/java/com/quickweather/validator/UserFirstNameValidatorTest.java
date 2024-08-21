package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class UserFirstNameValidatorTest {

    private UserValidator userValidator;
    private UserCreationRepository userCreationRepository;

    @BeforeEach
    void setup() {
        userCreationRepository = Mockito.mock(UserCreationRepository.class);
        userValidator = new UserValidator(userCreationRepository);
    }

    @Test
    void validate_whenFirstNameIsNull_thenReturnException() {
        UserDto userDto = new UserDto(null, "lastname", "pass", "email@email.pl", "2343234511");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validate_whenFirstNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "pass", "valid.email@example.com", "2343234511");

        assertDoesNotThrow(() -> userValidator.validate(userDto));
    }

    @Test
    void validate_whenFirstNameIsTooShort_thenThrowException() {
        UserDto userDto = new UserDto("f", "lastname", "pass", "valid.email@example.com", "2343234511");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validate_whenFirstNameIsTooLong_thenThrowException() {
        UserDto userDto = new UserDto("f".repeat(31), "lastname", "pass", "valid.email@example.com", "2343234511");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }
}