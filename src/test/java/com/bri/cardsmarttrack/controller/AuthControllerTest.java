package com.bri.cardsmarttrack.controller;

import com.bri.cardsmarttrack.dto.request.LoginRequest;
import com.bri.cardsmarttrack.dto.response.JwtAuthResponse;
import com.bri.cardsmarttrack.dto.response.UserProfileResponse;
import com.bri.cardsmarttrack.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login dengan kredensial valid mengembalikan 200 dan token")
    void testLogin_Success() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("nasabah@bri.co.id")
                .password("Password123!")
                .build();

        JwtAuthResponse response = JwtAuthResponse.builder()
                .token("mocked.jwt.token")
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .customer(UserProfileResponse.builder()
                        .id(1L)
                        .email("nasabah@bri.co.id")
                        .fullName("Budi Pratama")
                        .build())
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login berhasil"))
                .andExpect(jsonPath("$.data.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.customer.email").value("nasabah@bri.co.id"));
    }

    @Test
    @DisplayName("POST /api/auth/login dengan password salah mengembalikan 401 Unauthorized")
    void testLogin_BadCredentials_Returns401() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("nasabah@bri.co.id")
                .password("wrongpassword")
                .build();

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Email atau password salah")));
    }

    @Test
    @DisplayName("POST /api/auth/login dengan email kosong mengembalikan 400 Bad Request (validasi)")
    void testLogin_ValidationFailure_Returns400() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("") // Blank email
                .password("Password123!")
                .build();

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Validasi gagal"))
                .andExpect(jsonPath("$.data.email").exists());
    }
}
