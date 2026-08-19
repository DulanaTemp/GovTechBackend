package com.govtechparking.GovTechBackend.controller;

import com.govtechparking.GovTechBackend.dto.parking.CreateParkingLotRequest;
import com.govtechparking.GovTechBackend.dto.parking.NearbyParkingLotResponse;
import com.govtechparking.GovTechBackend.dto.parking.ParkingLotResponse;
import com.govtechparking.GovTechBackend.service.ParkingLotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/parking-lots")
@Validated
@Tag(name = "Parking Lots", description = "Proximity-based parking discovery and lot management")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    /**
     * Core endpoint: fetch available parking lots near the user.
     * Example: GET /api/v1/parking-lots/nearby?latitude=1.3521&longitude=103.8198&radiusKm=3
     */
    @GetMapping("/nearby")
    @Operation(summary = "Find nearby available parking",
            description = "Returns active lots with available spots within the radius (km), nearest first.")
    public List<NearbyParkingLotResponse> findNearby(
            @RequestParam @Min(-90) @Max(90) double latitude,
            @RequestParam @Min(-180) @Max(180) double longitude,
            @RequestParam(required = false) Double radiusKm) {
        return parkingLotService.findNearbyAvailable(latitude, longitude, radiusKm);
    }

    @GetMapping
    public List<ParkingLotResponse> findAll() {
        return parkingLotService.findAll();
    }

    @GetMapping("/{id}")
    public ParkingLotResponse findById(@PathVariable UUID id) {
        return parkingLotService.findById(id);
    }

    @PostMapping
    public ResponseEntity<ParkingLotResponse> create(@Valid @RequestBody CreateParkingLotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parkingLotService.create(request));
    }
}
