package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.domain.Role;
import com.quickweather.domain.RoleType;
import com.quickweather.domain.User;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;
import com.quickweather.validation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

    @Mock
    private UserValidator validator;

    @Mock
    private UserRepository userCreationRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserCreationService userCreationService;

    private UserDto createUserDto(String email, String password) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword(password);
        return userDto;
    }

    private User createUser(String email, String password) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }

    private Role createRole(RoleType roleType) {
        Role role = new Role();
        role.setRoleType(roleType);
        return role;
    }

    @Test
    void testCreateUser_WithValidData_ShouldSaveUser() {
        // Arrange
        UserDto userDto = createUserDto("test@example.com", "password");
        User user = createUser("test@example.com", "encodedPassword");
        Role mockRole = createRole(RoleType.USER);

        doNothing().when(validator).validate(userDto);
        doReturn(user).when(userMapper).toEntity(userDto);
        doReturn("encodedPassword").when(passwordEncoder).encode(userDto.getPassword());
        doReturn(Optional.of(mockRole)).when(roleRepository).findByRoleType(RoleType.USER);
        doReturn(user).when(userCreationRepository).save(any(User.class));

        // Act
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Assert
        verify(validator).validate(userDto);
        verify(userMapper).toEntity(userDto);
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(roleRepository).findByRoleType(RoleType.USER);
        verify(userCreationRepository).save(user);
    }

    @Test
    void testCreateUser_WithNonExistentDefaultRole_ShouldCreateDefaultRole() {
        // Arrange
        UserDto userDto = createUserDto("test@example.com", "password");
        User user = createUser("test@example.com", "encodedPassword");

        doNothing().when(validator).validate(userDto);
        doReturn(user).when(userMapper).toEntity(userDto);
        doReturn("encodedPassword").when(passwordEncoder).encode(userDto.getPassword());

        // Stubowanie braku domyślnej roli USER
        doReturn(Optional.empty()).when(roleRepository).findByRoleType(RoleType.USER);

        // Stubowanie tworzenia nowej roli USER
        Role newRole = new Role(1L, RoleType.USER, new HashSet<>());
        doReturn(newRole).when(roleRepository).save(any(Role.class));

        // Stubowanie zapisu użytkownika
        doReturn(user).when(userCreationRepository).save(any(User.class));

        // Act
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Assert
        verify(roleRepository).findByRoleType(RoleType.USER);
        verify(roleRepository).save(any(Role.class));
        verify(userCreationRepository).save(user);
    }

    @Test
    void testCreateUser_WithNullRoles_ShouldAssignDefaultRole() {
        // Arrange
        UserDto userDto = createUserDto("test@example.com", "password");
        userDto.setRoles(null);

        User user = createUser("test@example.com", "encodedPassword");
        Role mockDefaultRole = createRole(RoleType.USER);

        doNothing().when(validator).validate(userDto);
        doReturn(user).when(userMapper).toEntity(userDto);
        doReturn("encodedPassword").when(passwordEncoder).encode(userDto.getPassword());
        doReturn(Optional.of(mockDefaultRole)).when(roleRepository).findByRoleType(RoleType.USER);
        doReturn(user).when(userCreationRepository).save(any(User.class));

        // Act
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Assert
        verify(roleRepository).findByRoleType(RoleType.USER);
        verify(userCreationRepository).save(user);
    }
}