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
    private UserEmailValidator userEmailValidator;

    @Test
    void givenEmail_WhenEmailOk_thenDoesNotThrowException() {
        UserDto userDto = UserDto.builder()
                .email("first@wp.pl")
                .build();

        assertDoesNotThrow(() -> userEmailValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailNotOk_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .email("first.pl")
                .build();

        assertThrows(IllegalArgumentException.class, () -> userEmailValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailIsNull_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .email(null)
                .build();

        assertThrows(IllegalArgumentException.class, () -> userEmailValidator.validate(userDto));
    }

    @Test
    void givenEmail_WhenEmailExist_thenThrowException() {
        UserDto userDto = UserDto.builder()
                .email("first@wp.pl")
                .build();
        when(userCreationRepository.existsByEmail(userDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userEmailValidator.validate(userDto));
    }
}