package com.bri.cardsmarttrack.service;

import com.bri.cardsmarttrack.dto.response.SubscriptionResponse;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionResponse> getSubscriptionsByCard(Long cardId, Long customerId, SubscriptionStatus status);
    SubscriptionResponse unsubscribe(Long subscriptionId, Long customerId);
}
