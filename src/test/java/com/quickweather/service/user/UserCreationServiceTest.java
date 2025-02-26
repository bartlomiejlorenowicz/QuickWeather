package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.domain.Role;
import com.quickweather.domain.RoleType;
import com.quickweather.domain.User;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;
import com.quickweather.validation.user.user_creation.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private UserNotificationService userNotificationService;

    @InjectMocks
    private UserCreationService userCreationService;

    // Helper – ustawiamy również firstName oraz lastName, aby nie były null
    private UserDto createUserDto(String email, String password) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setPassword(password);
        userDto.setFirstName("TestFirstName");
        userDto.setLastName("TestLastName");
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
    void shouldSaveUserWhenDataIsValid() {
        // Arrange
        UserDto userDto = createUserDto("test@example.com", "password");
        User user = createUser("test@example.com", "encodedPassword");
        Role defaultRole = createRole(RoleType.USER);

        doNothing().when(validator).validate(userDto);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        // W tym teście weryfikujemy interakcję z userRoleService, a nie bezpośrednio z roleRepository
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Set<Role> roles = (Set<Role>) invocation.getArgument(0);
            roles.add(defaultRole);
            return null;
        }).when(userRoleService).assignDefaultUserRole(any(Set.class));
        when(userCreationRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(userNotificationService).sendWelcomeEmail(anyString(), anyString());

        // Act & Assert
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Verify – sprawdzamy wywołania kluczowych metod
        verify(validator).validate(userDto);
        verify(userMapper).toEntity(userDto);
        verify(passwordEncoder).encode(userDto.getPassword());
        verify(userRoleService).assignDefaultUserRole(any(Set.class));
        verify(userCreationRepository).save(user);
        verify(userNotificationService).sendWelcomeEmail(userDto.getEmail(), userDto.getFirstName());
    }

    @Test
    void shouldCreateDefaultRoleWhenNonExistent() {
        // Arrange
        UserDto userDto = createUserDto("test@example.com", "password");
        User user = createUser("test@example.com", "encodedPassword");

        doNothing().when(validator).validate(userDto);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        // Symulujemy brak domyślnej roli – nie musimy weryfikować roli z repozytorium, ponieważ
        // logika przypisania roli leży w userRoleService
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Set<Role> roles = (Set<Role>) invocation.getArgument(0);
            // Symulujemy stworzenie nowej roli i jej przypisanie
            Role newRole = new Role(1L, RoleType.USER, new HashSet<>());
            roles.add(newRole);
            return null;
        }).when(userRoleService).assignDefaultUserRole(any(Set.class));
        when(userCreationRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(userNotificationService).sendWelcomeEmail(anyString(), anyString());

        // Act & Assert
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Verify – sprawdzamy, że metoda przypisania domyślnej roli została wywołana,
        // a użytkownik został zapisany i wysłano e-mail powitalny
        verify(userRoleService).assignDefaultUserRole(any(Set.class));
        verify(userCreationRepository).save(user);
        verify(userNotificationService).sendWelcomeEmail(userDto.getEmail(), userDto.getFirstName());
    }

    @Test
    void shouldAssignDefaultRoleWhenUserDtoRolesAreNull() {
        // Arrange
        UserDto userDto = createUserDto("test@example.com", "password");
        // Ustawiamy role na null – DTO nie ma ról
        userDto.setRoles(null);
        User user = createUser("test@example.com", "encodedPassword");
        Role defaultRole = createRole(RoleType.USER);

        doNothing().when(validator).validate(userDto);
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");
        doAnswer(invocation -> {
            @SuppressWarnings("unchecked")
            Set<Role> roles = (Set<Role>) invocation.getArgument(0);
            roles.add(defaultRole);
            return null;
        }).when(userRoleService).assignDefaultUserRole(any(Set.class));
        when(userCreationRepository.save(any(User.class))).thenReturn(user);
        doNothing().when(userNotificationService).sendWelcomeEmail(anyString(), anyString());

        // Act & Assert
        assertDoesNotThrow(() -> userCreationService.createUser(userDto));

        // Verify – weryfikujemy, że przypisanie domyślnej roli oraz zapis użytkownika zostały wykonane
        verify(userRoleService).assignDefaultUserRole(any(Set.class));
        verify(userCreationRepository).save(user);
        verify(userNotificationService).sendWelcomeEmail(userDto.getEmail(), userDto.getFirstName());
    }
}
