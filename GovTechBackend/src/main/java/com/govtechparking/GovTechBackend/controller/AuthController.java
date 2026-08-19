package com.govtechparking.GovTechBackend.controller;

import com.govtechparking.GovTechBackend.dto.auth.AuthResponse;
import com.govtechparking.GovTechBackend.dto.auth.RequestOtpRequest;
import com.govtechparking.GovTechBackend.dto.auth.RequestOtpResponse;
import com.govtechparking.GovTechBackend.dto.auth.VerifyOtpRequest;
import com.govtechparking.GovTechBackend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Phone-number + OTP login flow")
@SecurityRequirements // public endpoints: no bearer token required
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/otp/request")
    @Operation(summary = "Request an OTP", description = "Generates a one-time code and sends it via SMS.")
    public RequestOtpResponse requestOtp(@Valid @RequestBody RequestOtpRequest request) {
        return authService.requestOtp(request);
    }

    @PostMapping("/otp/verify")
    @Operation(summary = "Verify an OTP",
            description = "Validates the OTP, provisions the user on first login, and returns a JWT.")
    public AuthResponse verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verifyOtp(request);
    }
}
