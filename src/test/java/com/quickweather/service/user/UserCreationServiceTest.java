package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.entity.Role;
import com.quickweather.entity.User;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.quickweather.validator.IntegrationTestConfig;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTest {

    @Mock
    private UserValidator validator;

    @Mock
    private UserCreationRepository userCreationRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserCreationService userCreationService;

    @Test
    void testCreateUser_WithValidData_ShouldSaveUser() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setRoles(Set.of(new Role(null, "USER")));

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword("encodedPassword");

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role(1L, "USER")));
        when(userCreationRepository.save(any(User.class))).thenReturn(user);

        // Act
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Assert
        verify(validator).validate(userDto);
        verify(userMapper).toEntity(userDto);
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(roleRepository).findByName("USER");
        verify(userCreationRepository).save(user);
    }

    @Test
    void testCreateUser_WithNonExistentRole_ShouldCreateNewRole() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setRoles(Set.of(new Role(null, "ADMIN")));

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword("encodedPassword");

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ADMIN")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenReturn(new Role(2L, "ADMIN"));
        when(userCreationRepository.save(any(User.class))).thenReturn(user);

        // Act
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Assert
        verify(roleRepository).findByName("ADMIN");
        verify(roleRepository).save(any(Role.class));
        verify(userCreationRepository).save(user);
    }

    @Test
    void testCreateUser_WithNullRoles_ShouldAssignDefaultRole() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setPassword("password");
        userDto.setRoles(null);

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setPassword("encodedPassword");

        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(new Role(1L, "USER")));
        when(userCreationRepository.save(any(User.class))).thenReturn(user);

        // Act
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Assert
        verify(roleRepository).findByName("USER");
        verify(userCreationRepository).save(user);
    }

}