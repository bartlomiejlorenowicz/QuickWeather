package com.quickweather.controllers;

import com.quickweather.domain.Role;
import com.quickweather.domain.User;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;
import com.quickweather.security.JwtTestUtil;
import com.quickweather.service.user.UserRoleService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

public abstract class BaseControllerTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtTestUtil jwtTestUtil;

    @Autowired
    protected UserRoleService userRoleService;

    @Autowired
    protected RoleRepository roleRepository;

    protected String tokenUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Set<Role> roles = new HashSet<>();
        userRoleService.assignDefaultUserRole(roles);

        User user = User.builder()
                .firstName("Adam")
                .lastName("Nowak")
                .email("adamnowak@wp.pl")
                .password(passwordEncoder.encode("testPassword"))
                .isEnabled(true)
                .roles(roles)
                .build();

        userRepository.save(user);

        tokenUser = jwtTestUtil.generateToken(user.getEmail(), "ROLE_USER");
    }
}
