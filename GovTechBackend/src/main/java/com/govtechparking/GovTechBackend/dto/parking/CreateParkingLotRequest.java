package com.govtechparking.GovTechBackend.dto.parking;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateParkingLotRequest(
        @NotBlank @Size(max = 150) String name,

        @NotNull
        @DecimalMin(value = "-90.0") @Digits(integer = 2, fraction = 8)
        BigDecimal latitude,

        @NotNull
        @DecimalMin(value = "-180.0") @Digits(integer = 3, fraction = 8)
        BigDecimal longitude,

        @NotNull @Min(0) Integer totalSpots,

        @NotNull @Min(0) Integer availableSpots,

        @NotNull @DecimalMin(value = "0.00") @Digits(integer = 6, fraction = 2)
        BigDecimal hourlyRate
) {
}
