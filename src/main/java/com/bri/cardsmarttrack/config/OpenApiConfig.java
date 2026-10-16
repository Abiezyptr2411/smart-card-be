package com.bri.cardsmarttrack.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Card Smart Track API - Bank Rakyat Indonesia (BRI)",
                version = "1.0.0",
                description = "REST API Backend untuk Card Smart Track BRI. Membantu nasabah memonitor kartu kredit, melacak subscription aktif per kartu, dan melakukan unsubscribe secara instan.",
                contact = @Contact(
                        name = "BRI Digital Banking Team",
                        email = "support@bri.co.id",
                        url = "https://bri.co.id"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server")
        },
        security = {
                @SecurityRequirement(name = "BearerAuth")
        }
)
@SecurityScheme(
        name = "BearerAuth",
        description = "Masukkan JWT token dengan format: Bearer <token>",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
