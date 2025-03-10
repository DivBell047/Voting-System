package com.example.NotificationService.model;

import lombok.Data;

@Data
public class VoteEvent {
    private Long userId;
    private Long candidateId;
    // Add other relevant fields from your vote event
}