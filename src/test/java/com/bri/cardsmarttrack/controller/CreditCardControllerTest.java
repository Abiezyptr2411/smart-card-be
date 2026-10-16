package com.bri.cardsmarttrack.controller;

import com.bri.cardsmarttrack.dto.response.CreditCardResponse;
import com.bri.cardsmarttrack.entity.enums.CardNetwork;
import com.bri.cardsmarttrack.entity.enums.CardStatus;
import com.bri.cardsmarttrack.exception.ForbiddenException;
import com.bri.cardsmarttrack.exception.ResourceNotFoundException;
import com.bri.cardsmarttrack.security.CustomUserDetailsService;
import com.bri.cardsmarttrack.security.JwtAuthenticationEntryPoint;
import com.bri.cardsmarttrack.security.JwtTokenProvider;
import com.bri.cardsmarttrack.security.UserPrincipal;
import com.bri.cardsmarttrack.service.CreditCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CreditCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreditCardService creditCardService;

    private UserPrincipal userPrincipal;

    @BeforeEach
    void setUp() {
        userPrincipal = UserPrincipal.builder()
                .id(1L)
                .email("nasabah@bri.co.id")
                .fullName("Budi Pratama")
                .password("pass")
                .authorities(Collections.emptyList())
                .build();
    }

    @Test
    @DisplayName("GET /api/cards tanpa token mengembalikan status 401 Unauthorized")
    void testGetCards_Unauthorized_Returns401() throws Exception {
        mockMvc.perform(get("/api/cards")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Akses tidak sah")));
    }

    @Test
    @DisplayName("GET /api/cards dengan login mengembalikan 200 dan nomor kartu ter-masking")
    void testGetCards_Success_ReturnsMaskedCards() throws Exception {
        CreditCardResponse cardDto = CreditCardResponse.builder()
                .id(1L)
                .cardName("BRI Touch Visa")
                .maskedCardNumber("**** **** **** 4321")
                .lastFourDigits("4321")
                .cardNetwork(CardNetwork.VISA)
                .creditLimit(new BigDecimal("25000000.00"))
                .availableLimit(new BigDecimal("18750000.00"))
                .expiryDate("10/28")
                .status(CardStatus.ACTIVE)
                .activeSubscriptionCount(3L)
                .totalActiveMonthlyAmount(new BigDecimal("407900.00"))
                .build();

        when(creditCardService.getCardsForCustomer(1L)).thenReturn(List.of(cardDto));

        mockMvc.perform(get("/api/cards")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].maskedCardNumber").value("**** **** **** 4321"))
                .andExpect(jsonPath("$.data[0].cardNumber").doesNotExist()) // Raw card number must never exist
                .andExpect(jsonPath("$.data[0].cardName").value("BRI Touch Visa"));
    }

    @Test
    @DisplayName("GET /api/cards/{cardId} dengan kartu milik orang lain mengembalikan 403 Forbidden")
    void testGetCardById_Forbidden_Returns403() throws Exception {
        when(creditCardService.getCardByIdForCustomer(eq(99L), eq(1L)))
                .thenThrow(new ForbiddenException("Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login"));

        mockMvc.perform(get("/api/cards/99")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Akses ditolak")));
    }

    @Test
    @DisplayName("GET /api/cards/{cardId} dengan ID kartu tidak ada mengembalikan 404 Not Found")
    void testGetCardById_NotFound_Returns404() throws Exception {
        when(creditCardService.getCardByIdForCustomer(eq(999L), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Kartu kredit", "id", 999L));

        mockMvc.perform(get("/api/cards/999")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("tidak ditemukan")));
    }
}
