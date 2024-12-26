package com.quickweather.service.user;

import com.quickweather.dto.user.UserDto;
import com.quickweather.entity.Role;
import com.quickweather.mapper.UserMapper;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.validator.UserValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


@Service
@Slf4j
public class UserCreationService {

    private final UserValidator validator;
    private final UserCreationRepository userCreationRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    public UserCreationService(UserValidator validator,
                               UserCreationRepository userCreationRepository,
                               UserMapper userMapper,
                               PasswordEncoder passwordEncoder,
                               RoleRepository roleRepository) {
        this.validator = validator;
        this.userCreationRepository = userCreationRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public void createUser(UserDto userDto) {
        validator.validate(userDto);
        log.info("starting saving user with mail:" + userDto.getEmail());

        var userEntity = userMapper.toEntity(userDto);
        userEntity.setPassword(passwordEncoder.encode(userDto.getPassword()));

        Set<Role> roles = new HashSet<>();
        if (userDto.getRoles() != null && !userDto.getRoles().isEmpty()) {
            // Pobranie ról z bazy danych lub utworzenie nowych, jeśli nie istnieją
            for (Role role : userDto.getRoles()) {
                Role existingRole = roleRepository.findByName(role.getName())
                        .orElseGet(() -> roleRepository.save(new Role(null, role.getName())));
                roles.add(existingRole);
            }
        } else {
            // Domyślna rola (USER), jeśli role nie są przekazane
            Role defaultRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "USER")));
            roles.add(defaultRole);
        }
        userEntity.setRoles(roles);

        // Zapis użytkownika w bazie danych
        log.info("Saving User entity to database: {}", userEntity);
        userCreationRepository.save(userEntity);
        log.info("User is saved with roles: {}", userEntity.getRoles());
    }
}
