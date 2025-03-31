package com.quickweather.service.user;

import com.google.api.services.gmail.Gmail;
import com.quickweather.domain.EmailTemplate;
import com.quickweather.integration.GmailQuickstart;
import com.quickweather.repository.EmailTemplateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @Mock
    private GmailQuickstart gmailQuickstart;

    @Mock
    private EmailTemplateRepository emailTemplateRepository;

    @InjectMocks
    private UserNotificationService userNotificationService;

    @Mock
    private Gmail gmailService;

    @Test
    void shouldSendWelcomeEmailUsingRepositoryTemplate() throws Exception {
        String recipientEmail = "bartek@wp.pl";
        String firstName = "Bartek";

        EmailTemplate template = EmailTemplate.builder()
                .subject("Subject1")
                .body("Hello %s, welcome!")
                .build();

        when(emailTemplateRepository.findByTemplateCode("WELCOME_EMAIL")).thenReturn(Optional.of(template));
        when(gmailQuickstart.getGmailService()).thenReturn(gmailService);

        userNotificationService.sendWelcomeEmail(recipientEmail, firstName);

        verify(gmailQuickstart).sendEmail(gmailService, recipientEmail, "Subject1", "Hello Bartek, welcome!");
    }

    @Test
    void shouldSendWelcomeEmailUsingDefaultTemplateWhenNotFound() throws Exception {
        String recipientEmail = "bartek@wp.pl";
        String firstName = "Bartek";

        when(emailTemplateRepository.findByTemplateCode("WELCOME_EMAIL")).thenReturn(Optional.empty());
        when(gmailQuickstart.getGmailService()).thenReturn(gmailService);

        userNotificationService.sendWelcomeEmail(recipientEmail, firstName);

        String expectedSubject = "Welcome to QuickWeather!";
        String expectedBody = String.format("Hi %s,\n\nThank you for registering with QuickWeather. We hope you enjoy using our service!", firstName);
        verify(gmailQuickstart).sendEmail(gmailService, recipientEmail, expectedSubject, expectedBody);
    }
}