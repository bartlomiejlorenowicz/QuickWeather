package com.quickweather.service;

import com.quickweather.dto.user.UserId;
import com.quickweather.entity.User;
import com.quickweather.exceptions.UserValidationException;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.service.user.UserStatusService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserStatusServiceTest {

    @Mock
    private UserCreationRepository userCreationRepository;

    @InjectMocks
    private UserStatusService userStatusService;

    @Test
    void shouldEnableUserSuccessfully() {
        User user = User.builder()
                .id(1L)
                .isEnabled(false)
                .build();

        UserId userId = new UserId(1L);

        when(userCreationRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userStatusService.enableUser(userId));
        assertTrue(user.isEnabled());
        verify(userCreationRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyEnabling() {
        User user = User.builder()
                .id(1L)
                .isEnabled(true)
                .build();

        UserId userId = new UserId(1L);

        when(userCreationRepository.findById(1L)).thenReturn(Optional.of(user));

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userStatusService.enableUser(userId));
        assertEquals("User is already enabled", exception.getMessage());
    }

    @Test
    void shouldDisableUserSuccessfully() {
        User user = User.builder()
                .id(1L)
                .isEnabled(true)
                .build();

        UserId userId = new UserId(1L);

        when(userCreationRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userStatusService.disabledUser(userId));
        assertFalse(user.isEnabled());
        verify(userCreationRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWhenDisablingAlreadyDisabledUser() {
        User user = User.builder()
                .id(1L)
                .isEnabled(false)
                .build();

        UserId userId = new UserId(1L);

        when(userCreationRepository.findById(1L)).thenReturn(Optional.of(user));

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userStatusService.disabledUser(userId));
        assertEquals("User is already disabled", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        UserId userId = new UserId(1L);

        when(userCreationRepository.findById(1L)).thenReturn(Optional.empty());

        UserValidationException exception = assertThrows(UserValidationException.class, () -> userStatusService.disabledUser(userId));
        assertEquals("User not found", exception.getMessage());
    }
}