package com.govtechparking.GovTechBackend.dto.ticket;

import com.govtechparking.GovTechBackend.entity.Ticket;
import com.govtechparking.GovTechBackend.enums.TicketStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketResponse(
        UUID id,
        UUID userId,
        UUID lotId,
        String lotName,
        String vehicleNumber,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        TicketStatus status,
        BigDecimal totalAmount,
        OffsetDateTime createdAt
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getUser().getId(),
                ticket.getLot().getId(),
                ticket.getLot().getName(),
                ticket.getVehicleNumber(),
                ticket.getStartTime(),
                ticket.getEndTime(),
                ticket.getStatus(),
                ticket.getTotalAmount(),
                ticket.getCreatedAt()
        );
    }
}
