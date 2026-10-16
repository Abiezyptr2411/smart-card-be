package com.bri.cardsmarttrack.service;

import com.bri.cardsmarttrack.dto.response.CreditCardResponse;

import java.util.List;

public interface CreditCardService {
    List<CreditCardResponse> getCardsForCustomer(Long customerId);
    CreditCardResponse getCardByIdForCustomer(Long cardId, Long customerId);
}
