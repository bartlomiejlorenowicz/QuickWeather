package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.entity.User;
import com.quickweather.repository.UserCreationRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
        UserDto userDto = new UserDto("firstname", null, "pass123", "email@wp.pl", "2345435465");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsTooShort_thenThrowException() {
        UserDto userDto = new UserDto("first", "l", "pass123", "email@wp.pl", "2345435465");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsTooLong_thenThrowException() {
        UserDto userDto = new UserDto("first", "l".repeat(31), "pass123", "email@wp.pl", "2345435465");

        Assertions.assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void validLastName_whenLastNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = new UserDto("first", "lastname", "pass123", "email@wp.pl", "2345435465");

        assertDoesNotThrow(() -> userValidator.validate(userDto));
    }


}