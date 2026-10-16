package com.bri.cardsmarttrack.dto.response;

import com.bri.cardsmarttrack.entity.CreditCard;
import com.bri.cardsmarttrack.entity.enums.CardNetwork;
import com.bri.cardsmarttrack.entity.enums.CardStatus;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Credit card information with masked card number")
public class CreditCardResponse {

    @Schema(description = "Card ID", example = "1")
    private Long id;

    @Schema(description = "Credit card product name", example = "BRI Touch Visa")
    private String cardName;

    @Schema(description = "Masked card number for security (full number is never exposed)", example = "**** **** **** 4321")
    private String maskedCardNumber;

    @Schema(description = "Last 4 digits of the card number", example = "4321")
    private String lastFourDigits;

    @Schema(description = "Card payment network", example = "VISA")
    private CardNetwork cardNetwork;

    @Schema(description = "Credit limit in IDR", example = "25000000.00")
    private BigDecimal creditLimit;

    @Schema(description = "Available credit limit in IDR", example = "18750000.00")
    private BigDecimal availableLimit;

    @Schema(description = "Card expiry date (MM/YY)", example = "10/28")
    private String expiryDate;

    @Schema(description = "Card status", example = "ACTIVE")
    private CardStatus status;

    @Schema(description = "Total number of active subscriptions tied to this card", example = "3")
    private Long activeSubscriptionCount;

    @Schema(description = "Estimated monthly spend on active subscriptions in IDR", example = "407900.00")
    private BigDecimal totalActiveMonthlyAmount;

    public static CreditCardResponse fromEntity(CreditCard card) {
        long activeCount = 0;
        BigDecimal totalMonthly = BigDecimal.ZERO;

        if (card.getSubscriptions() != null) {
            for (var sub : card.getSubscriptions()) {
                if (sub.getStatus() == SubscriptionStatus.ACTIVE) {
                    activeCount++;
                    if (sub.getBillingAmount() != null) {
                        totalMonthly = totalMonthly.add(sub.getBillingAmount());
                    }
                }
            }
        }

        return CreditCardResponse.builder()
                .id(card.getId())
                .cardName(card.getCardName())
                .maskedCardNumber(card.getMaskedCardNumber())
                .lastFourDigits(card.getLastFourDigits())
                .cardNetwork(card.getCardNetwork())
                .creditLimit(card.getCreditLimit())
                .availableLimit(card.getAvailableLimit())
                .expiryDate(card.getExpiryDate())
                .status(card.getStatus())
                .activeSubscriptionCount(activeCount)
                .totalActiveMonthlyAmount(totalMonthly)
                .build();
    }
}
