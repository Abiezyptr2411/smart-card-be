package com.bri.cardsmarttrack.controller;

import com.bri.cardsmarttrack.dto.response.ApiResponse;
import com.bri.cardsmarttrack.dto.response.SubscriptionResponse;
import com.bri.cardsmarttrack.entity.enums.SubscriptionStatus;
import com.bri.cardsmarttrack.security.UserPrincipal;
import com.bri.cardsmarttrack.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "3. Subscriptions", description = "Endpoint untuk melacak dan mengelola subscription/layanan berulang per kartu kredit")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(
            summary = "Ambil daftar layanan/subscription pada satu kartu",
            description = "Mengambil daftar subscription aktif (dan riwayat nonaktif) pada kartu kredit tertentu. Parameter query 'status' bersifat opsional untuk memfilter status (contoh: ACTIVE)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Daftar subscription berhasil diambil",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubscriptionResponse.class)))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Belum login atau token Bearer JWT tidak valid"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Kartu kredit bukan milik nasabah yang sedang login"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Kartu kredit tidak ditemukan"
            )
    })
    @GetMapping("/cards/{cardId}/subscriptions")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse>>> getSubscriptionsByCard(
            @Parameter(description = "ID kartu kredit", example = "1")
            @PathVariable Long cardId,
            @Parameter(description = "Filter opsional berdasarkan status langganan (ACTIVE atau INACTIVE)", example = "ACTIVE")
            @RequestParam(required = false) SubscriptionStatus status,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Request to get subscriptions for card ID: {} by customer ID: {} (filter: {})",
                cardId, userPrincipal.getId(), status);
        List<SubscriptionResponse> subscriptions = subscriptionService.getSubscriptionsByCard(cardId, userPrincipal.getId(), status);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil daftar layanan langganan", subscriptions));
    }

    @Operation(
            summary = "Unsubscribe satu layanan langganan",
            description = "Membatalkan langganan tertentu. Status subscription akan berubah menjadi INACTIVE dan dicatat timestamp pembatalannya."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Berhasil membatalkan langganan (status berubah menjadi INACTIVE)",
                    content = @Content(schema = @Schema(implementation = SubscriptionResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Belum login atau token Bearer JWT tidak valid"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Layanan terhubung pada kartu yang bukan milik nasabah yang sedang login"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Subscription tidak ditemukan"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "409",
                    description = "Layanan sudah berstatus INACTIVE (sudah pernah di-unsubscribe sebelumnya)"
            )
    })
    @PostMapping("/subscriptions/{id}/unsubscribe")
    public ResponseEntity<ApiResponse<SubscriptionResponse>> unsubscribe(
            @Parameter(description = "ID layanan subscription", example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Request to unsubscribe subscription ID: {} by customer ID: {}", id, userPrincipal.getId());
        SubscriptionResponse response = subscriptionService.unsubscribe(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Berhasil membatalkan langganan (unsubscribe)", response));
    }
}
