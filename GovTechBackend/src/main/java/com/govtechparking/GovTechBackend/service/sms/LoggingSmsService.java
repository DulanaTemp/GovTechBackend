package com.govtechparking.GovTechBackend.service.sms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Default SMS implementation that logs the message instead of calling a real
 * gateway. Swap this out (or add {@code @Primary} to another bean) when a real
 * provider is configured. Kept as the default so local/dev environments work
 * without external credentials.
 */
@Service
public class LoggingSmsService implements SmsService {

    private static final Logger log = LoggerFactory.getLogger(LoggingSmsService.class);

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("[SMS] to={} | body={}", phoneNumber, message);
    }
}
