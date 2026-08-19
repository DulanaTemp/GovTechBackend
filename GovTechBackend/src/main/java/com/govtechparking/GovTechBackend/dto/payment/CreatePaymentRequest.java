package com.govtechparking.GovTechBackend.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreatePaymentRequest(
        @NotNull UUID ticketId,

        @NotBlank @Size(max = 50) String paymentMethod
) {
}
