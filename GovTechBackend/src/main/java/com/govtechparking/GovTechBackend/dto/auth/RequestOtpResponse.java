package com.govtechparking.GovTechBackend.dto.auth;

import java.time.OffsetDateTime;

public record RequestOtpResponse(
        String message,
        OffsetDateTime expiresAt
) {
}
