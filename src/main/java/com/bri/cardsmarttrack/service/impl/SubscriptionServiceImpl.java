package com.bri.cardsmarttrack.service.impl;

import com.bri.cardsmarttrack.dto.request.CreateSubscriptionRequest;
import com.bri.cardsmarttrack.dto.response.SubscriptionResponse;
import com.bri.cardsmarttrack.entity.CreditCard;
import com.bri.cardsmarttrack.entity.Subscription;
import com.bri.cardsmarttrack.entity.enums.SubscriptionBillingPeriod;
import com.bri.cardsmarttrack.entity.enums.SubscriptionCategory;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import com.bri.cardsmarttrack.exception.ConflictException;
import com.bri.cardsmarttrack.exception.ForbiddenException;
import com.bri.cardsmarttrack.exception.ResourceNotFoundException;
import com.bri.cardsmarttrack.repository.CreditCardRepository;
import com.bri.cardsmarttrack.repository.SubscriptionRepository;
import com.bri.cardsmarttrack.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final CreditCardRepository creditCardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getSubscriptionsByCard(Long cardId, Long customerId, SubscriptionStatus status) {
        log.info("Fetching subscriptions for card ID: {} and customer ID: {} (status filter: {})",
                cardId, customerId, status);

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Kartu kredit", "id", cardId));

        if (!card.getCustomer().getId().equals(customerId)) {
            log.warn("Customer ID: {} forbidden from accessing card ID: {}", customerId, cardId);
            throw new ForbiddenException("Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login");
        }

        List<Subscription> subscriptions;
        if (status != null) {
            subscriptions = subscriptionRepository.findByCreditCardIdAndStatusOrderByNextBillingDateAsc(cardId, status);
        } else {
            subscriptions = subscriptionRepository.findByCreditCardIdOrderByNextBillingDateAsc(cardId);
        }

        return subscriptions.stream()
                .map(SubscriptionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubscriptionResponse createSubscription(Long cardId, Long customerId, CreateSubscriptionRequest request) {
        log.info("Creating new subscription '{}' on card ID: {} for customer ID: {}",
                request.getServiceName(), cardId, customerId);

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Kartu kredit", "id", cardId));

        if (!card.getCustomer().getId().equals(customerId)) {
            log.warn("Customer ID: {} forbidden from creating subscription on card ID: {}", customerId, cardId);
            throw new ForbiddenException("Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login");
        }

        LocalDate nextBilling = request.getNextBillingDate();
        if (nextBilling == null) {
            nextBilling = (request.getBillingPeriod() == SubscriptionBillingPeriod.YEARLY)
                    ? LocalDate.now().plusYears(1)
                    : LocalDate.now().plusMonths(1);
        }

        Subscription subscription = Subscription.builder()
                .creditCard(card)
                .serviceName(request.getServiceName().trim())
                .category(request.getCategory() != null ? request.getCategory() : SubscriptionCategory.OTHER)
                .billingAmount(request.getBillingAmount())
                .billingPeriod(request.getBillingPeriod() != null ? request.getBillingPeriod() : SubscriptionBillingPeriod.MONTHLY)
                .nextBillingDate(nextBilling)
                .status(SubscriptionStatus.ACTIVE)
                .logoUrl(request.getLogoUrl())
                .description(request.getDescription())
                .build();

        Subscription saved = subscriptionRepository.save(subscription);
        log.info("Successfully subscribed service '{}' (ID: {}) to card ending in {}",
                saved.getServiceName(), saved.getId(), card.getLastFourDigits());

        return SubscriptionResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public SubscriptionResponse subscribe(Long subscriptionId, Long customerId) {
        log.info("Attempting re-subscribe for subscription ID: {} by customer ID: {}", subscriptionId, customerId);

        Subscription subscription = subscriptionRepository.findByIdWithCardAndCustomer(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Layanan langganan", "id", subscriptionId));

        CreditCard card = subscription.getCreditCard();
        if (card == null || card.getCustomer() == null || !card.getCustomer().getId().equals(customerId)) {
            log.warn("Customer ID: {} forbidden from re-subscribing subscription ID: {}", customerId, subscriptionId);
            throw new ForbiddenException("Akses ditolak: Layanan langganan ini terhubung pada kartu yang bukan milik Anda");
        }

        if (subscription.getStatus() == SubscriptionStatus.ACTIVE) {
            log.warn("Subscription ID: {} is already active", subscriptionId);
            throw new ConflictException("Layanan '" + subscription.getServiceName() + "' sudah dalam status aktif");
        }

        // Re-activate subscription on the fly
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setUnsubscribedAt(null);
        if (subscription.getNextBillingDate() == null || subscription.getNextBillingDate().isBefore(LocalDate.now())) {
            subscription.setNextBillingDate(
                    subscription.getBillingPeriod() == SubscriptionBillingPeriod.YEARLY
                            ? LocalDate.now().plusYears(1)
                            : LocalDate.now().plusMonths(1)
            );
        }

        Subscription updated = subscriptionRepository.save(subscription);
        log.info("Successfully re-subscribed to service '{}' (ID: {}) on card ending in {}",
                updated.getServiceName(), updated.getId(), card.getLastFourDigits());

        return SubscriptionResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public SubscriptionResponse unsubscribe(Long subscriptionId, Long customerId) {
        log.info("Attempting unsubscribe for subscription ID: {} by customer ID: {}", subscriptionId, customerId);

        Subscription subscription = subscriptionRepository.findByIdWithCardAndCustomer(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Layanan langganan", "id", subscriptionId));

        CreditCard card = subscription.getCreditCard();
        if (card == null || card.getCustomer() == null || !card.getCustomer().getId().equals(customerId)) {
            log.warn("Customer ID: {} forbidden from unsubscribing subscription ID: {}", customerId, subscriptionId);
            throw new ForbiddenException("Akses ditolak: Layanan langganan ini terhubung pada kartu yang bukan milik Anda");
        }

        if (subscription.getStatus() == SubscriptionStatus.INACTIVE) {
            log.warn("Subscription ID: {} is already inactive", subscriptionId);
            throw new ConflictException("Layanan '" + subscription.getServiceName() + "' sudah tidak aktif (sudah pernah di-unsubscribe)");
        }

        subscription.unsubscribe();
        Subscription updated = subscriptionRepository.save(subscription);

        log.info("Successfully unsubscribed from service '{}' (ID: {}) on card ending in {}",
                updated.getServiceName(), updated.getId(), card.getLastFourDigits());

        return SubscriptionResponse.fromEntity(updated);
    }
}
