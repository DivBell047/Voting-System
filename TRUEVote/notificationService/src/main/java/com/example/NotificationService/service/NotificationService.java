package com.example.NotificationService.service;

import com.example.NotificationService.model.EmailEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    // Read the enabled flag from properties, default to true if not set
    @Value("${notification.enabled:true}")
    private boolean notificationEnabled;

    // Read the sender email address (must match the authenticated user)
    @Value("${spring.mail.username}")
    private String senderEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * Sends an email asynchronously based on the provided EmailEvent.
     * Checks the notification.enabled flag before sending.
     * Logs success or failure.
     *
     * @param emailEvent DTO containing recipient, subject, and body.
     */
    @Async // Execute this method in a separate thread
    public void sendEmail(EmailEvent emailEvent) {
        if (!notificationEnabled) {
            logger.info("Notifications are disabled globally. Email notification not sent for subject: {}", emailEvent.getSubject());
            return;
        }
        if (emailEvent == null || emailEvent.getRecipient() == null || emailEvent.getRecipient().isEmpty()) {
            logger.warn("Cannot send email: recipient is null or empty.");
            return;
        }

        logger.info("Attempting to send email to: {}, Subject: {}", emailEvent.getRecipient(), emailEvent.getSubject());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail); // Set the 'From' address
        message.setTo(emailEvent.getRecipient()); // Set the 'To' address from the event
        message.setSubject(emailEvent.getSubject()); // Set the subject
        message.setText(emailEvent.getBody()); // Set the email body

        try {
            javaMailSender.send(message); // Send the email using the configured mail sender
            logger.info("Email sent successfully to {}!", emailEvent.getRecipient());
        } catch (Exception e) {
            // Log the full exception stack trace for detailed debugging
            logger.error("Failed to send email to {}. Error: {}", emailEvent.getRecipient(), e.getMessage(), e);
        }
    }
}