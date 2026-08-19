package com.govtechparking.GovTechBackend.repository;

import com.govtechparking.GovTechBackend.entity.ParkingLot;
import com.govtechparking.GovTechBackend.repository.projection.NearbyParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, UUID> {

    /**
     * Finds active parking lots with at least one available spot within the given
     * radius (kilometres) of the supplied coordinates, ordered by distance.
     *
     * <p>Uses the Haversine formula (6371 km = Earth's mean radius) to compute the
     * great-circle distance directly in the database.
     */
    @Query(value = """
            SELECT
                pl.id AS id,
                pl.name AS name,
                pl.latitude AS latitude,
                pl.longitude AS longitude,
                pl.total_spots AS totalSpots,
                pl.available_spots AS availableSpots,
                pl.hourly_rate AS hourlyRate,
                (6371 * acos(
                    cos(radians(:latitude)) * cos(radians(pl.latitude)) *
                    cos(radians(pl.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(pl.latitude))
                )) AS distanceKm
            FROM parking_lots pl
            WHERE pl.is_active = TRUE
              AND pl.available_spots > 0
              AND (6371 * acos(
                    cos(radians(:latitude)) * cos(radians(pl.latitude)) *
                    cos(radians(pl.longitude) - radians(:longitude)) +
                    sin(radians(:latitude)) * sin(radians(pl.latitude))
                )) <= :radiusKm
            ORDER BY distanceKm ASC
            """, nativeQuery = true)
    List<NearbyParkingLot> findNearbyAvailable(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("radiusKm") double radiusKm);
}
