package com.bri.cardsmarttrack.controller;

import com.bri.cardsmarttrack.dto.response.SubscriptionResponse;
import com.bri.cardsmarttrack.entity.enums.SubscriptionBillingPeriod;
import com.bri.cardsmarttrack.entity.enums.SubscriptionCategory;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import com.bri.cardsmarttrack.exception.ConflictException;
import com.bri.cardsmarttrack.exception.ForbiddenException;
import com.bri.cardsmarttrack.exception.ResourceNotFoundException;
import com.bri.cardsmarttrack.security.UserPrincipal;
import com.bri.cardsmarttrack.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SubscriptionService subscriptionService;

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
    @DisplayName("GET /api/cards/{cardId}/subscriptions mengembalikan 403 jika kartu milik nasabah lain")
    void testGetSubscriptions_Forbidden_Returns403() throws Exception {
        when(subscriptionService.getSubscriptionsByCard(eq(99L), eq(1L), isNull()))
                .thenThrow(new ForbiddenException("Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login"));

        mockMvc.perform(get("/api/cards/99/subscriptions")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Akses ditolak")));
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/subscriptions mengembalikan 404 jika kartu tidak ditemukan")
    void testGetSubscriptions_NotFound_Returns404() throws Exception {
        when(subscriptionService.getSubscriptionsByCard(eq(999L), eq(1L), isNull()))
                .thenThrow(new ResourceNotFoundException("Kartu kredit", "id", 999L));

        mockMvc.perform(get("/api/cards/999/subscriptions")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("tidak ditemukan")));
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/subscriptions mengembalikan 200 dengan daftar subscription aktif")
    void testGetSubscriptions_Success_Returns200() throws Exception {
        SubscriptionResponse sub = SubscriptionResponse.builder()
                .id(1L)
                .cardId(1L)
                .serviceName("Netflix Premium")
                .category(SubscriptionCategory.ENTERTAINMENT)
                .billingAmount(new BigDecimal("186000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(10))
                .status(SubscriptionStatus.ACTIVE)
                .build();

        when(subscriptionService.getSubscriptionsByCard(eq(1L), eq(1L), isNull()))
                .thenReturn(List.of(sub));

        mockMvc.perform(get("/api/cards/1/subscriptions")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].serviceName").value("Netflix Premium"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /api/subscriptions/{id}/unsubscribe berhasil mengembalikan 200 dan status INACTIVE")
    void testUnsubscribe_Success_Returns200() throws Exception {
        SubscriptionResponse unsubscribed = SubscriptionResponse.builder()
                .id(1L)
                .cardId(1L)
                .serviceName("Netflix Premium")
                .category(SubscriptionCategory.ENTERTAINMENT)
                .billingAmount(new BigDecimal("186000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(10))
                .status(SubscriptionStatus.INACTIVE)
                .unsubscribedAt(LocalDateTime.now())
                .build();

        when(subscriptionService.unsubscribe(eq(1L), eq(1L))).thenReturn(unsubscribed);

        mockMvc.perform(post("/api/subscriptions/1/unsubscribe")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value(containsString("Berhasil membatalkan langganan")))
                .andExpect(jsonPath("$.data.status").value("INACTIVE"))
                .andExpect(jsonPath("$.data.unsubscribedAt").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/subscriptions/{id}/unsubscribe mengembalikan 409 Conflict jika sudah tidak aktif")
    void testUnsubscribe_Conflict_Returns409() throws Exception {
        when(subscriptionService.unsubscribe(eq(1L), eq(1L)))
                .thenThrow(new ConflictException("Layanan 'Netflix Premium' sudah tidak aktif (sudah pernah di-unsubscribe)"));

        mockMvc.perform(post("/api/subscriptions/1/unsubscribe")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("sudah tidak aktif")));
    }

    @Test
    @DisplayName("POST /api/subscriptions/{id}/unsubscribe mengembalikan 403 jika milik orang lain")
    void testUnsubscribe_Forbidden_Returns403() throws Exception {
        when(subscriptionService.unsubscribe(eq(99L), eq(1L)))
                .thenThrow(new ForbiddenException("Akses ditolak: Layanan langganan ini terhubung pada kartu yang bukan milik Anda"));

        mockMvc.perform(post("/api/subscriptions/99/unsubscribe")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("Akses ditolak")));
    }

    @Test
    @DisplayName("POST /api/subscriptions/{id}/unsubscribe mengembalikan 404 jika ID tidak ditemukan")
    void testUnsubscribe_NotFound_Returns404() throws Exception {
        when(subscriptionService.unsubscribe(eq(999L), eq(1L)))
                .thenThrow(new ResourceNotFoundException("Layanan langganan", "id", 999L));

        mockMvc.perform(post("/api/subscriptions/999/unsubscribe")
                        .with(user(userPrincipal))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("tidak ditemukan")));
    }
}
