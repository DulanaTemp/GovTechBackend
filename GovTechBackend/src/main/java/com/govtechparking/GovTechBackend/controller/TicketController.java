package com.govtechparking.GovTechBackend.controller;

import com.govtechparking.GovTechBackend.dto.ticket.CreateTicketRequest;
import com.govtechparking.GovTechBackend.dto.ticket.TicketResponse;
import com.govtechparking.GovTechBackend.security.CurrentUser;
import com.govtechparking.GovTechBackend.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tickets")
@Tag(name = "Tickets", description = "Parking session start, end, and lookup for the authenticated user")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @Operation(summary = "Start a parking session",
            description = "Selects a lot, records the start time, reserves a spot, and issues a ticket.")
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(CurrentUser.id(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a ticket by id")
    public TicketResponse findById(@PathVariable UUID id) {
        return ticketService.findById(CurrentUser.id(), id);
    }

    @GetMapping
    @Operation(summary = "List the authenticated user's tickets")
    public List<TicketResponse> findMine() {
        return ticketService.findByUser(CurrentUser.id());
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "End a parking session",
            description = "Ends the session, computes billable hours and total amount, and releases the spot.")
    public TicketResponse complete(@PathVariable UUID id) {
        return ticketService.complete(CurrentUser.id(), id);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an active parking session without charge")
    public TicketResponse cancel(@PathVariable UUID id) {
        return ticketService.cancel(CurrentUser.id(), id);
    }
}
