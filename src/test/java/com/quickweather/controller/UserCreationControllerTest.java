package com.quickweather.controller;

import com.quickweather.dto.user.UserDto;
import com.quickweather.entity.User;
import com.quickweather.repository.UserCreationRepository;
import com.quickweather.validator.IntegrationTestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


class UserCreationControllerTest extends IntegrationTestConfig {

    private static final String REGISTER_URL = "/api/v1/user/register";

    @Autowired
    private UserCreationRepository userCreationRepository;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .firstName("Andy")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john123@wp.pl")
                .phoneNumber("1234567890")
                .uuid(UUID.randomUUID())
                .build();

        userCreationRepository.save(user);
    }

    @AfterEach
    void clear() {
        userCreationRepository.deleteAll();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("John")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john1234@wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isCreated());

        User user = userCreationRepository.findByEmail(userDto.getEmail()).get();
        Assertions.assertNotNull(user.getUuid());

    }

    @Test
    void shouldFailWhenFirstNameIsInvalid() throws Exception {
        UserDto userDto = UserDto.builder()
                .firstName("A")
                .lastName("Murphy")
                .password("JohnP@ss123!")
                .email("john1235@wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post(REGISTER_URL)
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
                .email("john1236@wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post(REGISTER_URL)
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
                .email("john1237wp.pl")
                .phoneNumber("1234567890")
                .build();

        mockMvc.perform(post(REGISTER_URL)
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

        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("the given e-mail exists in the database"));
    }
}
