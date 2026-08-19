package com.govtechparking.GovTechBackend.dto.payment;

import com.govtechparking.GovTechBackend.entity.Payment;
import com.govtechparking.GovTechBackend.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID ticketId,
        UUID userId,
        BigDecimal amount,
        String paymentMethod,
        PaymentStatus status,
        String transactionRef,
        OffsetDateTime paidAt,
        OffsetDateTime createdAt
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getTicket().getId(),
                payment.getUser().getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionRef(),
                payment.getPaidAt(),
                payment.getCreatedAt()
        );
    }
}
