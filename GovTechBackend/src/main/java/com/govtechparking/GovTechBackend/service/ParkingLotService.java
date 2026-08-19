package com.govtechparking.GovTechBackend.service;

import com.govtechparking.GovTechBackend.dto.parking.CreateParkingLotRequest;
import com.govtechparking.GovTechBackend.dto.parking.NearbyParkingLotResponse;
import com.govtechparking.GovTechBackend.dto.parking.ParkingLotResponse;
import com.govtechparking.GovTechBackend.entity.ParkingLot;
import com.govtechparking.GovTechBackend.exception.BusinessException;
import com.govtechparking.GovTechBackend.exception.ResourceNotFoundException;
import com.govtechparking.GovTechBackend.repository.ParkingLotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ParkingLotService {

    private static final double DEFAULT_RADIUS_KM = 5.0;

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    /**
     * Core feature: return active parking lots that currently have available
     * spots within {@code radiusKm} of the user's coordinates, nearest first.
     */
    @Transactional(readOnly = true)
    public List<NearbyParkingLotResponse> findNearbyAvailable(double latitude, double longitude, Double radiusKm) {
        double radius = (radiusKm == null || radiusKm <= 0) ? DEFAULT_RADIUS_KM : radiusKm;
        return parkingLotRepository.findNearbyAvailable(latitude, longitude, radius).stream()
                .map(p -> new NearbyParkingLotResponse(
                        p.getId(),
                        p.getName(),
                        p.getLatitude(),
                        p.getLongitude(),
                        p.getTotalSpots(),
                        p.getAvailableSpots(),
                        p.getHourlyRate(),
                        p.getDistanceKm()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ParkingLotResponse> findAll() {
        return parkingLotRepository.findAll().stream()
                .map(ParkingLotResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ParkingLotResponse findById(UUID id) {
        return ParkingLotResponse.from(getLotOrThrow(id));
    }

    @Transactional
    public ParkingLotResponse create(CreateParkingLotRequest request) {
        if (request.availableSpots() > request.totalSpots()) {
            throw new BusinessException("availableSpots cannot exceed totalSpots");
        }
        ParkingLot lot = ParkingLot.builder()
                .name(request.name())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .totalSpots(request.totalSpots())
                .availableSpots(request.availableSpots())
                .hourlyRate(request.hourlyRate())
                .isActive(true)
                .build();
        return ParkingLotResponse.from(parkingLotRepository.save(lot));
    }

    private ParkingLot getLotOrThrow(UUID id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found: " + id));
    }
}
