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
@Schema(description = "Authentication response with JWT token")
public class JwtAuthResponse {

    @Schema(description = "JWT Access Token for authorization", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Token expiration duration in milliseconds", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Logged in customer profile")
    private UserProfileResponse customer;
}
