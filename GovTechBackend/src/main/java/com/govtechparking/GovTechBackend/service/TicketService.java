package com.govtechparking.GovTechBackend.service;

import com.govtechparking.GovTechBackend.dto.ticket.CreateTicketRequest;
import com.govtechparking.GovTechBackend.dto.ticket.TicketResponse;
import com.govtechparking.GovTechBackend.entity.ParkingLot;
import com.govtechparking.GovTechBackend.entity.Ticket;
import com.govtechparking.GovTechBackend.entity.User;
import com.govtechparking.GovTechBackend.enums.TicketStatus;
import com.govtechparking.GovTechBackend.exception.BusinessException;
import com.govtechparking.GovTechBackend.exception.ResourceNotFoundException;
import com.govtechparking.GovTechBackend.repository.ParkingLotRepository;
import com.govtechparking.GovTechBackend.repository.TicketRepository;
import com.govtechparking.GovTechBackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;

    public TicketService(TicketRepository ticketRepository,
                         UserRepository userRepository,
                         ParkingLotRepository parkingLotRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    /**
     * Opens a parking ticket and reserves a spot in the chosen lot by decrementing
     * its available spot count.
     */
    @Transactional
    public TicketResponse create(UUID userId, CreateTicketRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        ParkingLot lot = parkingLotRepository.findById(request.lotId())
                .orElseThrow(() -> new ResourceNotFoundException("Parking lot not found: " + request.lotId()));

        if (Boolean.FALSE.equals(lot.getIsActive())) {
            throw new BusinessException("Parking lot is not active");
        }
        if (lot.getAvailableSpots() <= 0) {
            throw new BusinessException("No available spots in this parking lot");
        }

        lot.setAvailableSpots(lot.getAvailableSpots() - 1);
        parkingLotRepository.save(lot);

        Ticket ticket = Ticket.builder()
                .user(user)
                .lot(lot)
                .vehicleNumber(request.vehicleNumber())
                .startTime(OffsetDateTime.now())
                .status(TicketStatus.ACTIVE)
                .build();
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    /**
     * Closes an active ticket: computes the fee from elapsed time and the lot's
     * hourly rate, then releases the reserved spot back to the lot.
     */
    @Transactional
    public TicketResponse complete(UUID userId, UUID ticketId) {
        Ticket ticket = getOwnedTicketOrThrow(userId, ticketId);
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new BusinessException("Only active tickets can be completed");
        }

        OffsetDateTime endTime = OffsetDateTime.now();
        ticket.setEndTime(endTime);
        ticket.setTotalAmount(calculateAmount(ticket, endTime));
        ticket.setStatus(TicketStatus.COMPLETED);

        releaseSpot(ticket.getLot());
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    /**
     * Cancels an active ticket without charge and releases the reserved spot.
     */
    @Transactional
    public TicketResponse cancel(UUID userId, UUID ticketId) {
        Ticket ticket = getOwnedTicketOrThrow(userId, ticketId);
        if (ticket.getStatus() != TicketStatus.ACTIVE) {
            throw new BusinessException("Only active tickets can be cancelled");
        }
        ticket.setEndTime(OffsetDateTime.now());
        ticket.setTotalAmount(BigDecimal.ZERO);
        ticket.setStatus(TicketStatus.CANCELLED);

        releaseSpot(ticket.getLot());
        return TicketResponse.from(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public TicketResponse findById(UUID userId, UUID ticketId) {
        return TicketResponse.from(getOwnedTicketOrThrow(userId, ticketId));
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> findByUser(UUID userId) {
        return ticketRepository.findByUserId(userId).stream()
                .map(TicketResponse::from)
                .toList();
    }

    private BigDecimal calculateAmount(Ticket ticket, OffsetDateTime endTime) {
        long minutes = Duration.between(ticket.getStartTime(), endTime).toMinutes();
        // Bill a minimum of one hour, rounding partial hours up.
        long billableHours = Math.max(1, (long) Math.ceil(minutes / 60.0));
        return ticket.getLot().getHourlyRate()
                .multiply(BigDecimal.valueOf(billableHours))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void releaseSpot(ParkingLot lot) {
        if (lot.getAvailableSpots() < lot.getTotalSpots()) {
            lot.setAvailableSpots(lot.getAvailableSpots() + 1);
            parkingLotRepository.save(lot);
        }
    }

    private Ticket getOwnedTicketOrThrow(UUID userId, UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + ticketId));
        if (!ticket.getUser().getId().equals(userId)) {
            // Do not reveal existence of another user's ticket.
            throw new ResourceNotFoundException("Ticket not found: " + ticketId);
        }
        return ticket;
    }
}
