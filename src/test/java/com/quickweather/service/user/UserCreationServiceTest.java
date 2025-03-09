package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.domain.User;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.UserRepository;
import com.quickweather.service.admin.SecurityEventService;
import com.quickweather.validation.user.user_creation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

    @Mock
    private UserValidator validator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private SecurityEventService securityEventService;

    @InjectMocks
    private UserCreationService userCreationService;

    private UserDto createUserDto(String email, String password) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setPassword(password);
        dto.setFirstName("John");
        dto.setLastName("Doe");
        return dto;
    }

    private User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    @Test
    void shouldSaveUserWhenDataIsValid() {
        UserDto dto = createUserDto("test@example.com", "password");
        User user = createUser("test@example.com", "oldPassword");

        doNothing().when(validator).validate(dto);
        when(userMapper.toEntity(dto)).thenReturn(user);

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        doAnswer(invocation -> {
            ((HashSet) invocation.getArgument(0)).add("DEFAULT_ROLE");
            return null;
        }).when(userRoleService).assignDefaultUserRole(any());

        doNothing().when(userNotificationService).sendWelcomeEmail(anyString(), anyString());
        when(securityEventService.getClientIpAddress()).thenReturn("127.0.0.1");
        doNothing().when(securityEventService).logEvent(anyString(), any(), anyString());

        assertDoesNotThrow(() -> userCreationService.createUser(dto));

        verify(validator).validate(dto);
        verify(userMapper).toEntity(dto);
        verify(passwordEncoder).encode("password");
        verify(userRoleService).assignDefaultUserRole(any());
        verify(userRepository).save(user);
        verify(userNotificationService).sendWelcomeEmail("test@example.com", "John");
        verify(securityEventService).logEvent(eq("test@example.com"), any(), eq("127.0.0.1"));
    }

    @Test
    void shouldCreateDefaultRoleWhenUserDtoRolesAreNull() {
        UserDto dto = createUserDto("test@example.com", "password");

        dto.setRoles(null);
        User user = createUser("test@example.com", "oldPassword");

        doNothing().when(validator).validate(dto);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        doAnswer(invocation -> {
            ((HashSet) invocation.getArgument(0)).add("DEFAULT_ROLE");
            return null;
        }).when(userRoleService).assignDefaultUserRole(any());
        when(userRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(userNotificationService).sendWelcomeEmail(anyString(), anyString());
        when(securityEventService.getClientIpAddress()).thenReturn("127.0.0.1");
        doNothing().when(securityEventService).logEvent(anyString(), any(), anyString());

        assertDoesNotThrow(() -> userCreationService.createUser(dto));

        verify(userRoleService).assignDefaultUserRole(any());
        verify(userRepository).save(user);
        verify(userNotificationService).sendWelcomeEmail("test@example.com", "John");
    }
}
