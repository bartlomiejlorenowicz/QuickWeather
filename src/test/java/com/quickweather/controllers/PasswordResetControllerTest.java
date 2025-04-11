package com.quickweather.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickweather.domain.User;
import com.quickweather.dto.apiResponse.OperationType;
import com.quickweather.dto.token.TokenRequest;
import com.quickweather.dto.user.EmailRequest;
import com.quickweather.dto.user.user_auth.SetNewPasswordRequest;
import com.quickweather.integration.GmailQuickstart;
import com.quickweather.security.JwtTestUtil;
import com.quickweather.validation.IntegrationTestConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(CommonTestSetupExtension.class)
class PasswordResetControllerTest extends IntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GmailQuickstart gmailQuickstart;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private String tokenUser;

    private User testUser;
    @RegisterExtension
    CommonTestSetupExtension setup = new CommonTestSetupExtension();

    @BeforeEach
    void setUp() {
        tokenUser = setup.getTokenUser();
        testUser = setup.getTestUser();
    }

    @Test
    void shouldResetPasswordSuccessfully() throws Exception {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("testUser@wp.pl");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset link sent"))
                .andExpect(jsonPath("$.operationType").value(OperationType.RESET_PASSWORD.name()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldSendForgotPasswordWhenUserForgotPassword() throws Exception {
        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail("testUser@wp.pl");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset link sent"))
                .andExpect(jsonPath("$.operationType").value(OperationType.FORGOT_PASSWORD.name()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldValidateResetTokenSuccessfully() throws Exception {
        String validResetToken = Jwts.builder()
                .setSubject("testUser@wp.pl")
                .claim("type", "reset-password")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minut
                .signWith(jwtTestUtil.getResetKey(), SignatureAlgorithm.HS256)
                .compact();

        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(validResetToken);

        jwtTestUtil.validateResetToken(tokenRequest.getToken());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/auth/validate-reset-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Token is valid"))
                .andExpect(jsonPath("$.operationType").value(OperationType.VALIDATE_RESET_TOKEN.name()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldSetNewPasswordSuccessfully() throws Exception {
        String resetToken = jwtTestUtil.generateResetToken(testUser);
        SetNewPasswordRequest request = new SetNewPasswordRequest();
        request.setNewPassword("Bartek123!");
        request.setConfirmPassword("Bartek123!");
        request.setToken(resetToken);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/user/auth/set-new-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password updated successfully."))
                .andExpect(jsonPath("$.operationType").value(OperationType.SET_NEW_PASSWORD.name()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}