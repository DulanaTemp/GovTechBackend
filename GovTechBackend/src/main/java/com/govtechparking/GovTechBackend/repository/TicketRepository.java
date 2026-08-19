package com.govtechparking.GovTechBackend.repository;

import com.govtechparking.GovTechBackend.entity.Ticket;
import com.govtechparking.GovTechBackend.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    List<Ticket> findByUserId(UUID userId);

    List<Ticket> findByUserIdAndStatus(UUID userId, TicketStatus status);
}
