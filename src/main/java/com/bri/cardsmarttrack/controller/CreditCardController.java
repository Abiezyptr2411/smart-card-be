package com.bri.cardsmarttrack.controller;

import com.bri.cardsmarttrack.dto.response.ApiResponse;
import com.bri.cardsmarttrack.dto.response.CreditCardResponse;
import com.bri.cardsmarttrack.security.UserPrincipal;
import com.bri.cardsmarttrack.service.CreditCardService;
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
@RequestMapping("/api/cards")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
@Tag(name = "2. Credit Cards", description = "Endpoint untuk melihat daftar dan detail kartu kredit milik nasabah")
public class CreditCardController {

    private final CreditCardService creditCardService;

    @Operation(
            summary = "Ambil semua kartu kredit milik nasabah",
            description = "Mengembalikan daftar semua kartu kredit milik nasabah yang sedang login. Nomor kartu selalu disamarkan (masked) untuk keamanan (contoh: **** **** **** 1234)."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Daftar kartu kredit berhasil diambil",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = CreditCardResponse.class)))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Belum login atau token Bearer JWT tidak valid"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<CreditCardResponse>>> getCustomerCards(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Request to get cards for customer ID: {}", userPrincipal.getId());
        List<CreditCardResponse> cards = creditCardService.getCardsForCustomer(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil daftar kartu kredit", cards));
    }

    @Operation(
            summary = "Ambil detail satu kartu kredit",
            description = "Mengembalikan detail satu kartu kredit berdasarkan ID. Memastikan kartu tersebut adalah milik nasabah yang sedang login."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Detail kartu kredit berhasil diambil"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Belum login atau token Bearer JWT tidak valid"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Kartu bukan milik nasabah yang sedang login"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Kartu kredit tidak ditemukan"
            )
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<ApiResponse<CreditCardResponse>> getCardById(
            @Parameter(description = "ID kartu kredit", example = "1")
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        log.info("Request to get card ID: {} for customer ID: {}", cardId, userPrincipal.getId());
        CreditCardResponse card = creditCardService.getCardByIdForCustomer(cardId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil detail kartu kredit", card));
    }
}
