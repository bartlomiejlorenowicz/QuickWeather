//package com.quickweather.service.user;
//
//import com.quickweather.dto.user.UserId;
//import com.quickweather.domain.User;
//import com.quickweather.exceptions.UserValidationException;
//import com.quickweather.repository.UserRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class UserStatusServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private UserSearchService userSearchService;
//
//    @Mock
//    private UserRoleService userRoleService;
//
//    @InjectMocks
//    private UserStatusService userStatusService;
//
//    @Test
//    void shouldEnableUserSuccessfully() {
//        // Arrange
//        User user = User.builder()
//                .id(1L)
//                .isEnabled(false)
//                .build();
//
//        UserId userId = new UserId(1L);
//
//        when(userSearchService.findById(1L)).thenReturn(user);
//
//        // Act & Assert
//        assertDoesNotThrow(() -> userStatusService.enableUser(userId));
//        assertTrue(user.isEnabled());
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void shouldThrowExceptionWhenUserAlreadyEnabled() {
//        // Arrange
//        User user = User.builder()
//                .id(1L)
//                .isEnabled(true)
//                .build();
//
//        UserId userId = new UserId(1L);
//
//        when(userSearchService.findById(1L)).thenReturn(user);
//
//        // Act
//        UserValidationException exception = assertThrows(UserValidationException.class, () -> userStatusService.enableUser(userId));
//
//        // Assert
//        assertEquals("User is already enabled", exception.getMessage());
//    }
//
//    @Test
//    void shouldDisableUserSuccessfully() {
//        // Arrange
//        User user = User.builder()
//                .id(1L)
//                .isEnabled(true)
//                .build();
//
//        UserId userId = new UserId(1L);
//
//        when(userSearchService.findById(1L)).thenReturn(user);
//
//        // Act & Assert
//        assertDoesNotThrow(() -> userStatusService.disableUser(userId));
//        assertFalse(user.isEnabled());
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void shouldThrowExceptionWhenUserAlreadyDisabled() {
//        // Arrange
//        User user = User.builder()
//                .id(1L)
//                .isEnabled(false)
//                .build();
//
//        UserId userId = new UserId(1L);
//
//        when(userSearchService.findById(1L)).thenReturn(user);
//
//        // Act
//        UserValidationException exception = assertThrows(UserValidationException.class, () -> userStatusService.disableUser(userId));
//
//        // Assert
//        assertEquals("User is already disabled", exception.getMessage());
//    }
//
//    @Test
//    void shouldThrowExceptionWhenUserNotFound() {
//        // Arrange
//        UserId userId = new UserId(1L);
//
//        when(userSearchService.findById(1L)).thenThrow(new UserValidationException(null, "User not found"));
//
//        // Act & Assert
//        UserValidationException exception = assertThrows(UserValidationException.class, () -> userStatusService.disableUser(userId));
//        assertEquals("User not found", exception.getMessage());
//    }
//}
