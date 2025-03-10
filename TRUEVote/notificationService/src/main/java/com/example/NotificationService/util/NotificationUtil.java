package com.example.NotificationService.util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


@Component
@Slf4j
public class NotificationUtil {

    @Autowired(required = false) // It's OK if email is not configured
    private JavaMailSender mailSender;

    @Value("${twilio.account.sid:#{null}}") // Use null as default so that checking process do the logic right if variables it's empty.
    private String twilioAccountSid;

    @Value("${twilio.auth.token:#{null}}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number:#{null}}")
    private String twilioPhoneNumber;

    public void sendEmail(String to, String subject, String text) {
        if (mailSender != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email sent to {}", to);
        } else {
            log.warn("JavaMailSender is not configured.  Email sending is disabled.");
        }
    }

    public void sendSMS(String to, String messageBody) {
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty() && // Also verify it isn't an empty string
                twilioAuthToken != null && !twilioAuthToken.isEmpty() &&
                twilioPhoneNumber != null && !twilioPhoneNumber.isEmpty()) {

            try {
                Twilio.init(twilioAccountSid, twilioAuthToken);

                Message message = Message.creator(
                                new PhoneNumber(to),  // to
                                new PhoneNumber(twilioPhoneNumber),  // from
                                messageBody)
                        .create();

                log.info("SMS sent to {} with SID: {}", to, message.getSid());
            } catch (Exception e) {
                log.error("Error sending SMS: {}", e.getMessage());
            }
        } else {
            log.warn("Twilio is not configured correctly. Ensure twilio.account.sid, twilio.auth.token and twilio.phone.number properties are set. SMS sending is disabled.");
        }
    }
}