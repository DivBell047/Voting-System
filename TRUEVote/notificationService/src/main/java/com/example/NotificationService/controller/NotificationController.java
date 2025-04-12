package com.example.NotificationService.controller;

import com.example.NotificationService.model.EmailEvent;
import com.example.NotificationService.model.VoteEvent;
import com.example.NotificationService.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications") // Base path for notification endpoints
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private NotificationService notificationService;

    // Inject a default recipient email address from properties for the /vote endpoint
    @Value("${notification.default.recipient:recipient@example.com}")
    private String defaultVoteRecipient;

    /**
     * Endpoint to trigger a notification based on a vote event.
     * Constructs an email and sends it directly using the default recipient.
     */
    @PostMapping("/vote")
    public ResponseEntity<String> handleVoteNotification(@RequestBody VoteEvent voteEvent) {
        logger.info("Received request to send vote notification: {}", voteEvent);
        if (voteEvent == null) {
            return ResponseEntity.badRequest().body("Vote event data cannot be null.");
        }
        try {
            // Construct the email content from the vote event
            String subject = "New Vote Cast!";
            String body = String.format(
                    "A vote has been cast for candidate %s (ID: %d) by user %s.",
                    voteEvent.getCandidateName(),
                    voteEvent.getCandidateId(),
                    voteEvent.getUserId()
            );

            // Create the EmailEvent DTO using the configured default recipient
            EmailEvent emailEvent = new EmailEvent(defaultVoteRecipient, subject, body);

            // Directly call the service to trigger the asynchronous email sending
            notificationService.sendEmail(emailEvent);

            logger.info("Vote notification email triggered for user {}", voteEvent.getUserId());
            // Return immediately - email sending is async
            return ResponseEntity.ok("Vote notification email triggered successfully.");

        } catch (Exception e) {
            logger.error("Error handling vote notification request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to trigger vote notification: " + e.getMessage());
        }
    }

    /**
     * Endpoint to directly trigger sending an email.
     * Expects recipient, subject, and body in the request body.
     */
    @PostMapping("/email")
    public ResponseEntity<String> handleDirectEmailNotification(@RequestBody EmailEvent emailEvent) {
        logger.info("Received request to send direct email: Subject='{}', Recipient='{}'",
                emailEvent.getSubject(), emailEvent.getRecipient());

        // Basic validation
        if (emailEvent == null) {
            return ResponseEntity.badRequest().body("Email event data cannot be null.");
        }
        if (emailEvent.getRecipient() == null || emailEvent.getRecipient().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Recipient email address is required.");
        }
        if (emailEvent.getSubject() == null || emailEvent.getSubject().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email subject is required.");
        }
        if (emailEvent.getBody() == null) {
            // Allow empty body, but maybe log a warning
            logger.warn("Received request to send email with null body to {}", emailEvent.getRecipient());
            emailEvent.setBody(""); // Ensure body is not null for mail sender
        }


        try {
            // Directly call the service to trigger the asynchronous email sending
            notificationService.sendEmail(emailEvent);

            logger.info("Direct email triggered for recipient {}", emailEvent.getRecipient());
            // Return immediately - email sending is async
            return ResponseEntity.ok("Direct email notification triggered successfully.");

        } catch (Exception e) {
            logger.error("Error handling direct email request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to trigger direct email notification: " + e.getMessage());
        }
    }
}