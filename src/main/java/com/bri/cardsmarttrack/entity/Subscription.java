package com.bri.cardsmarttrack.entity;

import com.bri.cardsmarttrack.entity.enums.SubscriptionBillingPeriod;
import com.bri.cardsmarttrack.entity.enums.SubscriptionCategory;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credit_card_id", nullable = false)
    private CreditCard creditCard;

    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    @Builder.Default
    private SubscriptionCategory category = SubscriptionCategory.OTHER;

    @Column(name = "billing_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal billingAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false, length = 20)
    @Builder.Default
    private SubscriptionBillingPeriod billingPeriod = SubscriptionBillingPeriod.MONTHLY;

    @Column(name = "next_billing_date", nullable = false)
    private LocalDate nextBillingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;

    public void unsubscribe() {
        this.status = SubscriptionStatus.INACTIVE;
        this.unsubscribedAt = LocalDateTime.now();
    }
}
