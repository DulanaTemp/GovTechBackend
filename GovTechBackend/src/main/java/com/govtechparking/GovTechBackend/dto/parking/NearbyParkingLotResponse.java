package com.govtechparking.GovTechBackend.dto.parking;

import java.math.BigDecimal;
import java.util.UUID;

public record NearbyParkingLotResponse(
        UUID id,
        String name,
        BigDecimal latitude,
        BigDecimal longitude,
        Integer totalSpots,
        Integer availableSpots,
        BigDecimal hourlyRate,
        Double distanceKm
) {
}
