package com.govtechparking.GovTechBackend.service;

import com.govtechparking.GovTechBackend.config.OtpProperties;
import com.govtechparking.GovTechBackend.dto.auth.AuthResponse;
import com.govtechparking.GovTechBackend.dto.auth.RequestOtpRequest;
import com.govtechparking.GovTechBackend.dto.auth.RequestOtpResponse;
import com.govtechparking.GovTechBackend.dto.auth.VerifyOtpRequest;
import com.govtechparking.GovTechBackend.dto.user.UserResponse;
import com.govtechparking.GovTechBackend.entity.OtpVerification;
import com.govtechparking.GovTechBackend.entity.User;
import com.govtechparking.GovTechBackend.exception.BusinessException;
import com.govtechparking.GovTechBackend.repository.OtpVerificationRepository;
import com.govtechparking.GovTechBackend.repository.UserRepository;
import com.govtechparking.GovTechBackend.security.JwtService;
import com.govtechparking.GovTechBackend.service.sms.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

/**
 * Handles the phone-number + OTP login journey:
 * <ol>
 *   <li>{@link #requestOtp} generates a one-time code, stores its hash, and sends it via SMS.</li>
 *   <li>{@link #verifyOtp} validates the code, provisions the user if new, and issues a JWT.</li>
 * </ol>
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final SecureRandom secureRandom = new SecureRandom();

    private final OtpVerificationRepository otpRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SmsService smsService;
    private final OtpProperties otpProperties;

    public AuthService(OtpVerificationRepository otpRepository,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       SmsService smsService,
                       OtpProperties otpProperties) {
        this.otpRepository = otpRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.smsService = smsService;
        this.otpProperties = otpProperties;
    }

    /**
     * Step 1: generate and dispatch an OTP for the supplied phone number.
     */
    @Transactional
    public RequestOtpResponse requestOtp(RequestOtpRequest request) {
        String otp = generateOtp();
        OffsetDateTime expiresAt = OffsetDateTime.now().plusMinutes(otpProperties.expiryMinutes());

        OtpVerification verification = OtpVerification.builder()
                .phoneNumber(request.phoneNumber())
                .otpHash(passwordEncoder.encode(otp))
                .expiresAt(expiresAt)
                .isVerified(false)
                .build();
        otpRepository.save(verification);

        smsService.sendSms(request.phoneNumber(),
                "Your GovTech Parking verification code is " + otp
                        + ". It expires in " + otpProperties.expiryMinutes() + " minutes.");

        if (otpProperties.logForDevelopment()) {
            log.warn("[DEV ONLY] OTP for {} is {}", request.phoneNumber(), otp);
        }

        return new RequestOtpResponse("OTP sent successfully", expiresAt);
    }

    /**
     * Step 2: verify the OTP, provision the user on first login, and return a JWT.
     */
    @Transactional
    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        OtpVerification verification = otpRepository
                .findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(request.phoneNumber())
                .orElseThrow(() -> new BusinessException("No pending OTP found. Please request a new one."));

        if (verification.getExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new BusinessException("OTP has expired. Please request a new one.");
        }
        if (!passwordEncoder.matches(request.otp(), verification.getOtpHash())) {
            throw new BusinessException("Invalid OTP.");
        }

        verification.setIsVerified(true);
        otpRepository.save(verification);

        User user = userRepository.findByPhoneNumber(request.phoneNumber())
                .orElseGet(() -> userRepository.save(User.builder()
                        .phoneNumber(request.phoneNumber())
                        .name(request.name())
                        .build()));

        // Backfill the name if the user was created earlier without one.
        if (user.getName() == null && request.name() != null) {
            user.setName(request.name());
            userRepository.save(user);
        }

        String token = jwtService.generateToken(user.getId(), user.getPhoneNumber());
        return new AuthResponse(token, "Bearer", jwtService.getExpirationMinutes(), UserResponse.from(user));
    }

    private String generateOtp() {
        int length = otpProperties.length();
        int bound = (int) Math.pow(10, length);
        int number = secureRandom.nextInt(bound);
        return String.format("%0" + length + "d", number);
    }
}
