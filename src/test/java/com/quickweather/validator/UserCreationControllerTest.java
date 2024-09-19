package com.quickweather.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserCreationControllerTest extends IntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("John")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john123@wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.message").value("User successfully registered"));
    }
}
