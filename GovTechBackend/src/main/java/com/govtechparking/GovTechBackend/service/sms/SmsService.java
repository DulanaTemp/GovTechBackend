package com.govtechparking.GovTechBackend.service.sms;

/**
 * Abstraction over the SMS provider so the gateway (Twilio, AWS SNS, a local
 * telco aggregator, etc.) can be swapped without touching business logic.
 */
public interface SmsService {

    /**
     * Sends an SMS message to the given phone number.
     *
     * @param phoneNumber recipient in E.164 format (e.g. +6591234567)
     * @param message     the message body
     */
    void sendSms(String phoneNumber, String message);
}
