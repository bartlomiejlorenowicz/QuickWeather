package com.quickweather.service.user;

import com.google.api.services.gmail.Gmail;
import com.quickweather.integration.GmailQuickstart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserNotificationService {
    private final GmailQuickstart gmailQuickstart;

    public UserNotificationService(GmailQuickstart gmailQuickstart) {
        this.gmailQuickstart = gmailQuickstart;
    }

    /**
     * Sends a welcome email to the newly registered user.
     *
     * @param recipientEmail the email of the new user
     * @param firstName      the user's first name
     */
    public void sendWelcomeEmail(String recipientEmail, String firstName) {
        try {
            Gmail service = gmailQuickstart.getGmailService();
            String subject = "Welcome to QuickWeather!";
            String body = String.format(
                    "Hi %s,\n\nThank you for registering with QuickWeather. We hope you enjoy using our service!",
                    firstName
            );

            gmailQuickstart.sendEmail(service, recipientEmail, subject, body);
            log.info("Welcome email sent to: {}", recipientEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", recipientEmail, e.getMessage(), e);
        }
    }
}
