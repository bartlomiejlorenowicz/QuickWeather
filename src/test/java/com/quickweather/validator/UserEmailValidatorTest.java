package com.quickweather.validator;

import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserEmailValidatorTest {

    private UserCreationRepository userCreationRepository;
    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userCreationRepository = Mockito.mock(UserCreationRepository.class);
        userValidator = new UserValidator(userCreationRepository);
    }

    @Test
    void givenEmail_WhenEmailOk_thenDoesNotThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "pass123!", "first@wp.pl", "1234567891");

        assertDoesNotThrow(() -> userValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailNotOk_thenThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "pass123!", "first.pl", "1234567891");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailIsNull_thenThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "pass123!", null, "1234567891");

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailExist_thenThrowException() {
        UserDto userDto = new UserDto("firstname", "lastname", "pass123!", "first@wp.pl", "1234567891");
        when(userCreationRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userValidator.validate(userDto));
    }


}