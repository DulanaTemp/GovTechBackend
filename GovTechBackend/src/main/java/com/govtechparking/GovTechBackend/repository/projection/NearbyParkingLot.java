package com.govtechparking.GovTechBackend.repository.projection;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Spring Data projection for the nearby-available parking lot query, including
 * the computed distance from the search point.
 */
public interface NearbyParkingLot {

    UUID getId();

    String getName();

    BigDecimal getLatitude();

    BigDecimal getLongitude();

    Integer getTotalSpots();

    Integer getAvailableSpots();

    BigDecimal getHourlyRate();

    Double getDistanceKm();
}
