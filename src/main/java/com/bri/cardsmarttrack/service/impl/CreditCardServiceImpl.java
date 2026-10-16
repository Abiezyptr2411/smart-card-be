package com.bri.cardsmarttrack.service.impl;

import com.bri.cardsmarttrack.dto.response.CreditCardResponse;
import com.bri.cardsmarttrack.entity.CreditCard;
import com.bri.cardsmarttrack.exception.ForbiddenException;
import com.bri.cardsmarttrack.exception.ResourceNotFoundException;
import com.bri.cardsmarttrack.repository.CreditCardRepository;
import com.bri.cardsmarttrack.service.CreditCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CreditCardResponse> getCardsForCustomer(Long customerId) {
        log.info("Fetching credit cards for customer ID: {}", customerId);
        List<CreditCard> cards = creditCardRepository.findByCustomerIdWithSubscriptions(customerId);
        return cards.stream()
                .map(CreditCardResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CreditCardResponse getCardByIdForCustomer(Long cardId, Long customerId) {
        log.info("Fetching card ID: {} for customer ID: {}", cardId, customerId);

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Kartu kredit", "id", cardId));

        if (!card.getCustomer().getId().equals(customerId)) {
            log.warn("Customer ID: {} attempted to access card ID: {} belonging to another customer",
                    customerId, cardId);
            throw new ForbiddenException("Akses ditolak: Kartu kredit ini bukan milik nasabah yang sedang login");
        }

        return CreditCardResponse.fromEntity(card);
    }
}
