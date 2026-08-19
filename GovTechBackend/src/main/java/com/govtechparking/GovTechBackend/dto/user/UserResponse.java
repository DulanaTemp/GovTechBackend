package com.govtechparking.GovTechBackend.dto.user;

import com.govtechparking.GovTechBackend.entity.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String phoneNumber,
        String name,
        OffsetDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getPhoneNumber(),
                user.getName(),
                user.getCreatedAt()
        );
    }
}
