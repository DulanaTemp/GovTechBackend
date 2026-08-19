package com.govtechparking.GovTechBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OTP configuration. Values are bound from the {@code app.otp.*} namespace.
 */
@ConfigurationProperties(prefix = "app.otp")
public record OtpProperties(
        int length,
        long expiryMinutes,
        boolean logForDevelopment
) {
}
