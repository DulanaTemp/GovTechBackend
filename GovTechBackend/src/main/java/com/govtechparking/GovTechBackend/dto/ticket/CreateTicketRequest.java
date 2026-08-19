package com.govtechparking.GovTechBackend.dto.ticket;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateTicketRequest(
        @NotNull UUID lotId,

        @Size(max = 20) String vehicleNumber
) {
}
