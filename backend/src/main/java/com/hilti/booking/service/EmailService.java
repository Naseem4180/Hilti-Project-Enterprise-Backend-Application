package com.hilti.booking.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
    public void sendEmail(String recipient, String subject, String body) {
        // Placeholder email service: logs the simulated email delivery.
        System.out.printf("[EMAIL] to=%s subject=%s body=%s\n", recipient, subject, body);
    }
}
