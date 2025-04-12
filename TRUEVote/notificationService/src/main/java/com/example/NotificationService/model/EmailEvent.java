package com.example.NotificationService.model;

import java.util.Objects;

public class EmailEvent {
    private String recipient;
    private String subject;
    private String body;

    // No-argument constructor
    public EmailEvent() {
    }

    // All-arguments constructor
    public EmailEvent(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    // Getters
    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    // Setters
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setBody(String body) {
        this.body = body;
    }

    // equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailEvent that = (EmailEvent) o;
        return Objects.equals(recipient, that.recipient) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(body, that.body);
    }

    // hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(recipient, subject, body);
    }

    // toString()
    @Override
    public String toString() {
        // Limit body length in toString for logging clarity
        String bodyPreview = (body != null && body.length() > 50) ? body.substring(0, 50) + "..." : body;
        return "EmailEvent{" +
                "recipient='" + recipient + '\'' +
                ", subject='" + subject + '\'' +
                ", bodyPreview='" + bodyPreview + '\'' +
                '}';
    }
}