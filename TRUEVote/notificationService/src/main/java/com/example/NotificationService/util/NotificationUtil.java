package com.example.NotificationService.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;


@Component
@Slf4j
public class NotificationUtil {

    @Autowired(required = false) // It's OK if email is not configured
    private JavaMailSender mailSender;

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
}