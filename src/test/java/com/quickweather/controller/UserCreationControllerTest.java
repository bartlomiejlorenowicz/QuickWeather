package com.quickweather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.dto.UserDto;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.service.UserCreationService;
import com.quickweather.validator.IntegrationTestConfig;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserCreationControllerTest extends IntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserCreationRepository userCreationRepository;

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

    @Test
    void shouldFailWhenFirstNameIsInvalid() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("A")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john123@wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.message").value("first name must have at least 2 letters"));
    }

    @Test
    void shouldFailWhenLastNameIsInvalid() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("Andy")
                .lastName("M".repeat(31))
                .password("JohnP@ss123!")
                .email("john123@wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.message").value("last name must have maximum 30 letters"));
    }

    @Test
    void shouldFailWhenEmailIsNotInvalid() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("Andy")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john123wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content()
                        .contentType("application/json"))
                .andExpect(jsonPath("$.message").value("email is not valid"));
    }

    @Test
    void shouldFailWhenEmailAlreadyExist() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("Andy")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john123@wp.pl")
                .phoneNumber("1234567890")
                .build();

        when(userCreationRepository.existsByEmail("john123@wp.pl")).thenReturn(true);

        mockMvc.perform(post("/api/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("the given e-mail exists in the database"));
    }
}
