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
    private UserFirstNameValidator userFirstNameValidator;

    @Test
    void validateFirstName_whenFirstNameIsNull_thenReturnException() {
        UserDto userDto = UserDto.builder()
                .firstName(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> userFirstNameValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("firstname")
                .build();

        assertDoesNotThrow(() -> userFirstNameValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsTooShort_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("f")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userFirstNameValidator.validate(userDto));
    }

    @Test
    void validateFirstName_whenFirstNameIsTooLong_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .firstName("f".repeat(31))
                .build();

        assertThrows(IllegalArgumentException.class, () -> userFirstNameValidator.validate(userDto));
    }
}