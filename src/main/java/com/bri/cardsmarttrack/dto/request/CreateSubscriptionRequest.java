package com.bri.cardsmarttrack.dto.request;

import com.bri.cardsmarttrack.entity.enums.SubscriptionBillingPeriod;
import com.bri.cardsmarttrack.entity.enums.SubscriptionCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request body untuk subscribe layanan baru pada kartu kredit")
public class CreateSubscriptionRequest {

    @NotBlank(message = "Nama layanan/merchant tidak boleh kosong")
    @Schema(description = "Nama merchant/layanan langganan", example = "Disney+ Hotstar")
    private String serviceName;

    @Schema(description = "Kategori langganan", example = "ENTERTAINMENT")
    @Builder.Default
    private SubscriptionCategory category = SubscriptionCategory.ENTERTAINMENT;

    @NotNull(message = "Nominal tagihan tidak boleh kosong")
    @DecimalMin(value = "1000.00", message = "Nominal tagihan minimal Rp 1.000")
    @Schema(description = "Nominal biaya langganan per siklus (IDR)", example = "65000.00")
    private BigDecimal billingAmount;

    @Schema(description = "Siklus periode tagihan", example = "MONTHLY")
    @Builder.Default
    private SubscriptionBillingPeriod billingPeriod = SubscriptionBillingPeriod.MONTHLY;

    @Schema(description = "Tanggal tagihan berikutnya (format: YYYY-MM-DD)", example = "2026-11-20")
    private LocalDate nextBillingDate;

    @Schema(description = "URL logo/ikon merchant", example = "https://www.hotstar.com/assets/favicon-32x32.png")
    private String logoUrl;

    @Schema(description = "Deskripsi paket langganan", example = "Paket Langganan Premium Bulanan")
    private String description;
}
