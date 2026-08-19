package com.govtechparking.GovTechBackend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RequestOtpRequest(
        @NotBlank
        @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must be a valid phone number")
        String phoneNumber
) {
}
