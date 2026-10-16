package com.bri.cardsmarttrack.controller;

import com.bri.cardsmarttrack.dto.request.LoginRequest;
import com.bri.cardsmarttrack.dto.response.ApiResponse;
import com.bri.cardsmarttrack.dto.response.JwtAuthResponse;
import com.bri.cardsmarttrack.dto.response.UserProfileResponse;
import com.bri.cardsmarttrack.security.UserPrincipal;
import com.bri.cardsmarttrack.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "1. Authentication", description = "Endpoint autentikasi nasabah dan manajemen sesi JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Login nasabah",
            description = "Melakukan autentikasi menggunakan email dan password, mengembalikan token JWT untuk autorisasi endpoint lainnya."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Login berhasil, token JWT dikembalikan",
                    content = @Content(schema = @Schema(implementation = JwtAuthResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Format email tidak valid atau request body tidak lengkap"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Email atau password salah"
            )
    })
    @SecurityRequirements // Public endpoint, no Bearer token required
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for email: {}", request.getEmail());
        JwtAuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login berhasil", authResponse));
    }

    @Operation(
            summary = "Profil nasabah saat ini",
            description = "Mengambil profil nasabah yang sedang login berdasarkan token Bearer JWT."
    )
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        UserProfileResponse profile = authService.getCurrentUser(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success("Berhasil mengambil data nasabah", profile));
    }
}
