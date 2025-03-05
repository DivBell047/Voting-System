package com.example.TRUEVote.service; // Changed package name

import com.example.TRUEVote.model.VoteEvent; // Changed package name
import com.example.TRUEVote.model.Notification; // Changed package name
import com.example.TRUEVote.repository.NotificationRepository; // Changed package name
import com.example.TRUEVote.util.NotificationUtil; // Changed package name
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Autowired
    private NotificationUtil notificationUtil;

    @Autowired(required = false)
    private NotificationRepository notificationRepository;

    public void processVoteEvent(VoteEvent voteEvent) {
        // 1. Fetch user details (name, email, phone) based on voteEvent.getUserId()
        //    You'll likely need to call the User Service via REST or gRPC for this.
        //    Example (using a hypothetical UserService client):
        //    User user = userServiceClient.getUser(voteEvent.getUserId());

        //For simplicity, we will mock the user data
        String userEmail = "test@example.com";
        String userPhoneNumber = "+1234567890";

        // 2. Construct the notification message.
        String message = "Thank you for voting!";

        // 3. Send email notification.
        try {
            notificationUtil.sendEmail(userEmail, "Voting Confirmation", message);
            log.info("Email notification sent to {}", userEmail);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
        }

        // 4. Send SMS notification.
        try {
            notificationUtil.sendSMS(userPhoneNumber, message);
            log.info("SMS notification sent to {}", userPhoneNumber);
        } catch (Exception e) {
            log.error("Error sending SMS: {}", e.getMessage());
        }

        // 5. Save notification to database (optional).
        if (notificationRepository != null) {
            Notification notification = new Notification();
            notification.setUserId(voteEvent.getUserId());
            notification.setMessage(message);
            notificationRepository.save(notification);
        }
    }
}