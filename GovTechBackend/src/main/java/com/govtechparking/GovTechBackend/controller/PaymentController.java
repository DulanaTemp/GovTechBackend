package com.govtechparking.GovTechBackend.controller;

import com.govtechparking.GovTechBackend.dto.payment.CreatePaymentRequest;
import com.govtechparking.GovTechBackend.dto.payment.PaymentResponse;
import com.govtechparking.GovTechBackend.security.CurrentUser;
import com.govtechparking.GovTechBackend.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@Tag(name = "Payments", description = "Payment processing and receipts for the authenticated user")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Pay for a completed ticket",
            description = "Processes payment for the ticket's total amount and sends an SMS receipt on success.")
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(CurrentUser.id(), request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a payment by id")
    public PaymentResponse findById(@PathVariable UUID id) {
        return paymentService.findById(CurrentUser.id(), id);
    }

    @GetMapping
    @Operation(summary = "List the authenticated user's payments")
    public List<PaymentResponse> findMine() {
        return paymentService.findByUser(CurrentUser.id());
    }
}
