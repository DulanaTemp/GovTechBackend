package com.govtechparking.GovTechBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT configuration. Values are bound from the {@code app.jwt.*} namespace.
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes
) {
}
