package com.bri.cardsmarttrack.dto.response;

import com.bri.cardsmarttrack.entity.Subscription;
import com.bri.cardsmarttrack.entity.enums.SubscriptionBillingPeriod;
import com.bri.cardsmarttrack.entity.enums.SubscriptionCategory;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Subscription details associated with a credit card")
public class SubscriptionResponse {

    @Schema(description = "Subscription ID", example = "1")
    private Long id;

    @Schema(description = "Credit Card ID linked to this subscription", example = "1")
    private Long cardId;

    @Schema(description = "Name of the subscription service or merchant", example = "Netflix Premium")
    private String serviceName;

    @Schema(description = "Subscription category", example = "ENTERTAINMENT")
    private SubscriptionCategory category;

    @Schema(description = "Recurring billing amount in IDR", example = "186000.00")
    private BigDecimal billingAmount;

    @Schema(description = "Billing frequency period", example = "MONTHLY")
    private SubscriptionBillingPeriod billingPeriod;

    @Schema(description = "Upcoming billing date", example = "2026-11-05")
    private LocalDate nextBillingDate;

    @Schema(description = "Current subscription status", example = "ACTIVE")
    private SubscriptionStatus status;

    @Schema(description = "Brand logo / icon URL", example = "https://assets.nflxext.com/ffe/siteui/common/icons/monogram/netflix.png")
    private String logoUrl;

    @Schema(description = "Optional description or plan details", example = "Ultra HD 4K, 4 Screens")
    private String description;

    @Schema(description = "Timestamp when the service was unsubscribed, if inactive", example = "2026-10-16T10:15:30")
    private LocalDateTime unsubscribedAt;

    public static SubscriptionResponse fromEntity(Subscription subscription) {
        return SubscriptionResponse.builder()
                .id(subscription.getId())
                .cardId(subscription.getCreditCard() != null ? subscription.getCreditCard().getId() : null)
                .serviceName(subscription.getServiceName())
                .category(subscription.getCategory())
                .billingAmount(subscription.getBillingAmount())
                .billingPeriod(subscription.getBillingPeriod())
                .nextBillingDate(subscription.getNextBillingDate())
                .status(subscription.getStatus())
                .logoUrl(subscription.getLogoUrl())
                .description(subscription.getDescription())
                .unsubscribedAt(subscription.getUnsubscribedAt())
                .build();
    }
}
