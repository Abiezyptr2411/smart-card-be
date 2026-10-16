package com.bri.cardsmarttrack.entity;

import com.bri.cardsmarttrack.entity.enums.CardNetwork;
import com.bri.cardsmarttrack.entity.enums.CardStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "card_name", nullable = false, length = 100)
    private String cardName;

    // Full card number stored securely in DB, never exposed via API
    @Column(name = "card_number", nullable = false, length = 20)
    private String cardNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "card_network", nullable = false, length = 20)
    private CardNetwork cardNetwork;

    @Column(name = "credit_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "available_limit", nullable = false, precision = 15, scale = 2)
    private BigDecimal availableLimit;

    @Column(name = "expiry_date", nullable = false, length = 7) // e.g. "12/28"
    private String expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CardStatus status = CardStatus.ACTIVE;

    @Builder.Default
    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscriptions = new ArrayList<>();

    public void addSubscription(Subscription subscription) {
        subscriptions.add(subscription);
        subscription.setCreditCard(this);
    }

    /**
     * Returns masked card number in format: **** **** **** 1234
     * Full card number is never exposed to the client.
     */
    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "**** **** **** ****";
        }
        String lastFour = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + lastFour;
    }

    /**
     * Returns only the last 4 digits
     */
    public String getLastFourDigits() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return cardNumber.substring(cardNumber.length() - 4);
    }
}
