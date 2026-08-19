package com.govtechparking.GovTechBackend.dto.parking;

import com.govtechparking.GovTechBackend.entity.ParkingLot;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record ParkingLotResponse(
        UUID id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer totalSpots,
        Integer availableSpots,
        BigDecimal hourlyRate,
        Boolean isActive,
        OffsetDateTime createdAt
) {
    public static ParkingLotResponse from(ParkingLot lot) {
        return new ParkingLotResponse(
                lot.getId(),
                lot.getName(),
                lot.getLatitude(),
                lot.getLongitude(),
                lot.getTotalSpots(),
                lot.getAvailableSpots(),
                lot.getHourlyRate(),
                lot.getIsActive(),
                lot.getCreatedAt()
        );
    }
}
