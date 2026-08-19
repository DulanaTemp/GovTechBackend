package com.govtechparking.GovTechBackend.service;

import com.govtechparking.GovTechBackend.dto.payment.CreatePaymentRequest;
import com.govtechparking.GovTechBackend.dto.payment.PaymentResponse;
import com.govtechparking.GovTechBackend.entity.Payment;
import com.govtechparking.GovTechBackend.entity.Ticket;
import com.govtechparking.GovTechBackend.enums.PaymentStatus;
import com.govtechparking.GovTechBackend.exception.BusinessException;
import com.govtechparking.GovTechBackend.exception.ResourceNotFoundException;
import com.govtechparking.GovTechBackend.repository.PaymentRepository;
import com.govtechparking.GovTechBackend.repository.TicketRepository;
import com.govtechparking.GovTechBackend.service.sms.SmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final DateTimeFormatter RECEIPT_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final SmsService smsService;

    public PaymentService(PaymentRepository paymentRepository,
                          TicketRepository ticketRepository,
                          SmsService smsService) {
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.smsService = smsService;
    }

    /**
     * Creates a payment for a ticket's outstanding amount. In a real system the
     * gateway call would be asynchronous; here we mark it SUCCESS immediately and
     * generate a transaction reference.
     */
    @Transactional
    public PaymentResponse create(UUID userId, CreatePaymentRequest request) {
        Ticket ticket = ticketRepository.findById(request.ticketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found: " + request.ticketId()));

        if (!ticket.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Ticket not found: " + request.ticketId());
        }
        if (ticket.getTotalAmount() == null) {
            throw new BusinessException("Ticket has no billable amount yet; complete the ticket first");
        }
        paymentRepository.findByTicketId(ticket.getId()).ifPresent(p -> {
            throw new BusinessException("A payment already exists for this ticket");
        });

        BigDecimal amount = ticket.getTotalAmount();
        Payment payment = Payment.builder()
                .ticket(ticket)
                .user(ticket.getUser())
                .amount(amount)
                .paymentMethod(request.paymentMethod())
                .status(PaymentStatus.SUCCESS)
                .transactionRef("TXN-" + UUID.randomUUID())
                .paidAt(OffsetDateTime.now())
                .build();
        Payment saved = paymentRepository.save(payment);

        // Step 6: on successful payment, notify the user with a receipt via SMS.
        if (saved.getStatus() == PaymentStatus.SUCCESS) {
            sendReceiptSms(saved, ticket);
        }

        return PaymentResponse.from(saved);
    }

    private void sendReceiptSms(Payment payment, Ticket ticket) {
        String lotName = ticket.getLot().getName();
        String message = String.format(
                "GovTech Parking receipt: Ticket %s at %s. Amount paid: $%s via %s. Ref: %s. Paid at %s. Thank you!",
                ticket.getId(),
                lotName,
                payment.getAmount().toPlainString(),
                payment.getPaymentMethod(),
                payment.getTransactionRef(),
                payment.getPaidAt().format(RECEIPT_TIME));
        smsService.sendSms(ticket.getUser().getPhoneNumber(), message);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(UUID userId, UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        if (!payment.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Payment not found: " + id);
        }
        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> findByUser(UUID userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(PaymentResponse::from)
                .toList();
    }
}
