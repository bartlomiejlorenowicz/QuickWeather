package com.quickweather.controllers;

import com.quickweather.dto.user.UserId;
import com.quickweather.domain.Role;
import com.quickweather.domain.RoleType;
import com.quickweather.domain.User;
import com.quickweather.repository.RoleRepository;
import com.quickweather.repository.UserRepository;
import com.quickweather.security.JwtUtil;
import com.quickweather.service.user.CustomUserDetails;
import com.quickweather.service.user.CustomUserDetailsService;
import com.quickweather.validation.IntegrationTestConfig;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class UserAccountStatusControllerIntegrationTest extends IntegrationTestConfig {

    @Autowired
    private UserRepository userCreationRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void cleanUpAndSetupRoles() {
        userCreationRepository.deleteAll();
        roleRepository.deleteAll();

        roleRepository.save(Role.builder().roleType(RoleType.ADMIN).build());
        roleRepository.save(Role.builder().roleType(RoleType.USER).build());
    }

    @Test
    void shouldEnableUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .isEnabled(false)
                .phoneNumber("1234567890")
                .uuid(UUID.randomUUID())
                .roles(Set.of(
                        roleRepository.findByRoleType(RoleType.USER).orElseThrow()
                ))
                .build();

        user = userCreationRepository.save(user);

        CustomUserDetails customUserDetails = userDetailsService.createCustomUserDetails(user);

        Map<String, Object> tokenResponse = jwtUtil.generateToken(customUserDetails, user.getUuid());
        String token = (String) tokenResponse.get("token");


        mockMvc.perform(put("/api/v1/user/account-status/enable")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(new UserId(user.getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updatedUser = userCreationRepository.findById(user.getId()).orElseThrow();
        assertTrue(updatedUser.isEnabled(), "User should be enabled");
    }

    @Test
    void shouldDisableUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("password")
                .isEnabled(false)
                .phoneNumber("1234567890")
                .uuid(UUID.randomUUID())
                .roles(Set.of(
                        roleRepository.findByRoleType(RoleType.USER).orElseThrow()
                ))
                .build();

        user = userCreationRepository.save(user);

        CustomUserDetails customUserDetails = userDetailsService.createCustomUserDetails(user);

        Map<String, Object> tokenResponse = jwtUtil.generateToken(customUserDetails, user.getUuid());
        String token = (String) tokenResponse.get("token");

        mockMvc.perform(put("/api/v1/user/account-status/disable").content(objectMapper.writeValueAsString(new UserId(user.getId())))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updatedUser = userCreationRepository.findById(user.getId()).orElseThrow();
        assertFalse(updatedUser.isEnabled(), "User should be disabled");
    }

    @Test
    void shouldReturnBadRequestUserDoesNotExist() throws Exception {

        UserId nonExistentUserId = new UserId(999L);

        User adminUser = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email("admin@example.com")
                .password("password")
                .isEnabled(true)
                .uuid(UUID.randomUUID())
                .roles(Set.of(
                        roleRepository.findByRoleType(RoleType.ADMIN).orElseThrow()
                ))
                .build();

        adminUser = userCreationRepository.save(adminUser);

        CustomUserDetails customUserDetails = userDetailsService.createCustomUserDetails(adminUser);
        Map<String, Object> tokenResponse = jwtUtil.generateToken(customUserDetails, adminUser.getUuid());
        String token = (String) tokenResponse.get("token");

        mockMvc.perform(put("/api/v1/user/account-status/disable")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(nonExistentUserId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}