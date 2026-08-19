package com.govtechparking.GovTechBackend.repository;

import com.govtechparking.GovTechBackend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByTicketId(UUID ticketId);

    List<Payment> findByUserId(UUID userId);
}
