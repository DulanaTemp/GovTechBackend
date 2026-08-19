package com.govtechparking.GovTechBackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * OTP configuration. Values are bound from the {@code app.otp.*} namespace.
 */
@ConfigurationProperties(prefix = "app.otp")
public record OtpProperties(
        int length,
        long expiryMinutes,
        boolean logForDevelopment,
        /**
         * When true, OTP verification accepts any correctly-formatted code without
         * checking it against a generated OTP. DEV/DEMO ONLY. Must be false in production.
         */
        boolean bypassValidation
) {
}
