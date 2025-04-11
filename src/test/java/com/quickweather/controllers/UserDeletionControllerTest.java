package com.quickweather.controllers;

import com.quickweather.domain.User;
import com.quickweather.validation.IntegrationTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(CommonTestSetupExtension.class)
class UserDeletionControllerTest extends IntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @RegisterExtension
    CommonTestSetupExtension setup = new CommonTestSetupExtension();

    private String tokenUser;
    private User testUser;

    @BeforeEach
    void setUp() {
        tokenUser = setup.getTokenUser();
        testUser = setup.getTestUser();
    }

    @Test
    void shouldDeleteUserReturnSuccessfullyResponse() throws Exception {
        Long testUserId = testUser.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/delete-user/{userId}", testUserId)
                        .header("Authorization", "Bearer " + tokenUser)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User has been deleted"))
                .andExpect(jsonPath("$.operationType").value("DELETE_ACCOUNT"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
