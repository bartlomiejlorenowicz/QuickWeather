package com.quickweather.controller;

import com.quickweather.dto.user.Role;
import com.quickweather.dto.user.UserId;
import com.quickweather.entity.User;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.security.JwtUtil;
import com.quickweather.validator.IntegrationTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAccountStatusControllerIntegrationTest extends IntegrationTestConfig {

    @Autowired
    private UserCreationRepository userCreationRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    void cleanUpDatabase() {
        userCreationRepository.deleteAll();
    }

    @Test
    void shouldEnableUser() throws Exception {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johhnun123.doe@example.com")
                .password("password")
                .isEnabled(false)
                .role(Role.ADMIN)
                .phoneNumber("1234567890")
                .uuid(UUID.randomUUID())
                .build();

        user = userCreationRepository.save(user);

        String token = jwtUtil.generateToken("johhnun123.doe@example.com");

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
                .email("johhnun123.doe@example.com")
                .password("password")
                .isEnabled(true)
                .role(Role.ADMIN)
                .phoneNumber("1234567890")
                .uuid(UUID.randomUUID())
                .build();

        user = userCreationRepository.save(user);

        String token = jwtUtil.generateToken("johhnun123.doe@example.com");

        mockMvc.perform(put("/api/v1/user/account-status/disable").content(objectMapper.writeValueAsString(new UserId(user.getId())))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updatedUser = userCreationRepository.findById(user.getId()).orElseThrow();
        assertFalse(updatedUser.isEnabled(), "User should be disabled");
    }

    @Test
    void shouldReturnBadRequestUserDoesNotExist() throws Exception {

        UserId userId = new UserId(11L);

        String token = jwtUtil.generateToken("johhnun123.doe@example.com");

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("johhnun123.doe@example.com")
                .password("password")
                .isEnabled(true)
                .role(Role.ADMIN)
                .phoneNumber("1234567890")
                .uuid(UUID.randomUUID())
                .build();

        userCreationRepository.save(user);

        mockMvc.perform(put("/api/v1/user/account-status/disable").content(objectMapper.writeValueAsString(userId))
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}