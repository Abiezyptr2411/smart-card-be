package com.bri.cardsmarttrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer login request payload")
public class LoginRequest {

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Schema(description = "Email nasabah yang terdaftar", example = "nasabah@bri.co.id")
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Schema(description = "Password akun nasabah", example = "Password123!")
    private String password;
}
