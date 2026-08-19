package com.govtechparking.GovTechBackend.dto.auth;

import com.govtechparking.GovTechBackend.dto.user.UserResponse;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInMinutes,
        UserResponse user
) {
}
