package com.bri.cardsmarttrack.service;

import com.bri.cardsmarttrack.dto.response.SubscriptionResponse;
import com.bri.cardsmarttrack.entity.CreditCard;
import com.bri.cardsmarttrack.entity.Customer;
import com.bri.cardsmarttrack.entity.Subscription;
import com.bri.cardsmarttrack.entity.enums.*;
import com.bri.cardsmarttrack.exception.ConflictException;
import com.bri.cardsmarttrack.exception.ForbiddenException;
import com.bri.cardsmarttrack.exception.ResourceNotFoundException;
import com.bri.cardsmarttrack.repository.CreditCardRepository;
import com.bri.cardsmarttrack.repository.SubscriptionRepository;
import com.bri.cardsmarttrack.service.impl.SubscriptionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private CreditCardRepository creditCardRepository;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private Customer customer1;
    private Customer customer2;
    private CreditCard card1;
    private Subscription activeSubscription;
    private Subscription inactiveSubscription;

    @BeforeEach
    void setUp() {
        customer1 = Customer.builder()
                .email("nasabah@bri.co.id")
                .fullName("Budi Pratama")
                .password("encoded_pass")
                .build();
        customer1.setId(1L);

        customer2 = Customer.builder()
                .email("siti@bri.co.id")
                .fullName("Siti Rahma")
                .password("encoded_pass")
                .build();
        customer2.setId(2L);

        card1 = CreditCard.builder()
                .cardName("BRI Touch Visa")
                .cardNumber("4000123456784321")
                .cardNetwork(CardNetwork.VISA)
                .creditLimit(new BigDecimal("25000000.00"))
                .availableLimit(new BigDecimal("18750000.00"))
                .expiryDate("10/28")
                .customer(customer1)
                .build();
        card1.setId(10L);

        activeSubscription = Subscription.builder()
                .serviceName("Netflix Premium")
                .category(SubscriptionCategory.ENTERTAINMENT)
                .billingAmount(new BigDecimal("186000.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().plusDays(10))
                .status(SubscriptionStatus.ACTIVE)
                .creditCard(card1)
                .build();
        activeSubscription.setId(100L);

        inactiveSubscription = Subscription.builder()
                .serviceName("Spotify Family")
                .category(SubscriptionCategory.MUSIC)
                .billingAmount(new BigDecimal("86900.00"))
                .billingPeriod(SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(LocalDate.now().minusDays(2))
                .status(SubscriptionStatus.INACTIVE)
                .creditCard(card1)
                .build();
        inactiveSubscription.setId(101L);
    }

    @Test
    @DisplayName("Unsubscribe berhasil ketika subscription aktif dan milik customer yang login")
    void testUnsubscribe_Success() {
        when(subscriptionRepository.findByIdWithCardAndCustomer(100L))
                .thenReturn(Optional.of(activeSubscription));
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SubscriptionResponse response = subscriptionService.unsubscribe(100L, 1L);

        assertNotNull(response);
        assertEquals(SubscriptionStatus.INACTIVE, response.getStatus());
        assertNotNull(response.getUnsubscribedAt());
        verify(subscriptionRepository, times(1)).save(activeSubscription);
    }

    @Test
    @DisplayName("Unsubscribe melempar 404 ketika ID subscription tidak ditemukan")
    void testUnsubscribe_NotFound_ThrowsResourceNotFoundException() {
        when(subscriptionRepository.findByIdWithCardAndCustomer(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                subscriptionService.unsubscribe(999L, 1L)
        );
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unsubscribe melempar 403 Forbidden ketika kartu bukan milik nasabah yang login")
    void testUnsubscribe_Forbidden_ThrowsForbiddenException() {
        when(subscriptionRepository.findByIdWithCardAndCustomer(100L))
                .thenReturn(Optional.of(activeSubscription));

        // Customer 2 (Siti) trying to unsubscribe Customer 1's subscription
        assertThrows(ForbiddenException.class, () ->
                subscriptionService.unsubscribe(100L, 2L)
        );
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Unsubscribe melempar 409 Conflict ketika subscription sudah berstatus INACTIVE")
    void testUnsubscribe_AlreadyInactive_ThrowsConflictException() {
        when(subscriptionRepository.findByIdWithCardAndCustomer(101L))
                .thenReturn(Optional.of(inactiveSubscription));

        assertThrows(ConflictException.class, () ->
                subscriptionService.unsubscribe(101L, 1L)
        );
        verify(subscriptionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ambil subscriptions berhasil ketika kartu milik nasabah")
    void testGetSubscriptionsByCard_Success() {
        when(creditCardRepository.findById(10L)).thenReturn(Optional.of(card1));
        when(subscriptionRepository.findByCreditCardIdOrderByNextBillingDateAsc(10L))
                .thenReturn(List.of(activeSubscription, inactiveSubscription));

        List<SubscriptionResponse> results = subscriptionService.getSubscriptionsByCard(10L, 1L, null);

        assertEquals(2, results.size());
        assertEquals("Netflix Premium", results.get(0).getServiceName());
    }

    @Test
    @DisplayName("Ambil subscriptions melempar 403 Forbidden ketika kartu bukan milik nasabah")
    void testGetSubscriptionsByCard_Forbidden() {
        when(creditCardRepository.findById(10L)).thenReturn(Optional.of(card1));

        // Customer 2 trying to access card 1 of Customer 1
        assertThrows(ForbiddenException.class, () ->
                subscriptionService.getSubscriptionsByCard(10L, 2L, null)
        );
    }

    @Test
    @DisplayName("Ambil subscriptions melempar 404 ketika kartu tidak ditemukan")
    void testGetSubscriptionsByCard_NotFound() {
        when(creditCardRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                subscriptionService.getSubscriptionsByCard(999L, 1L, null)
        );
    }
}
