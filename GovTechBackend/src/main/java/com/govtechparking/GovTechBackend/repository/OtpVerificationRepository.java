package com.govtechparking.GovTechBackend.repository;

import com.govtechparking.GovTechBackend.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, UUID> {

    /**
     * Returns the most recent unverified, non-expired OTP for a phone number.
     */
    Optional<OtpVerification> findTopByPhoneNumberAndIsVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);
}
