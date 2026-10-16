package com.bri.cardsmarttrack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer profile information")
public class UserProfileResponse {

    @Schema(description = "Customer ID", example = "1")
    private Long id;

    @Schema(description = "Customer email address", example = "nasabah@bri.co.id")
    private String email;

    @Schema(description = "Customer full name", example = "Budi Pratama")
    private String fullName;

    @Schema(description = "Customer phone number", example = "+6281234567890")
    private String phoneNumber;
}
