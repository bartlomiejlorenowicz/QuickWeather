package com.quickweather.controller;

import com.quickweather.dto.user.UserId;
import com.quickweather.entity.User;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.validator.IntegrationTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAccountStatusControllerIntegrationTest extends IntegrationTestConfig {

    @Autowired
    private UserCreationRepository userCreationRepository;

    @BeforeEach
    void cleanUpDatabase() {
        userCreationRepository.deleteAll();
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
                .build();

        user = userCreationRepository.save(user);

        mockMvc.perform(put("/api/v1/user/account-status/enable").content(objectMapper.writeValueAsString(new UserId(user.getId())))
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
                .isEnabled(true)
                .phoneNumber("1234567890")
                .build();

        user = userCreationRepository.save(user);

        mockMvc.perform(put("/api/v1/user/account-status/disable").content(objectMapper.writeValueAsString(new UserId(user.getId())))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User updatedUser = userCreationRepository.findById(user.getId()).orElseThrow();
        assertFalse(updatedUser.isEnabled(), "User should be disabled");
    }

    @Test
    void shouldReturnBadRequestUserDoesNotExist() throws Exception {

        UserId userId = new UserId(11L);

        mockMvc.perform(put("/api/v1/user/account-status/disable").content(objectMapper.writeValueAsString(userId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}