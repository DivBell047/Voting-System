package com.example.TRUEVote.consumer; // Changed package name

import com.example.TRUEVote.model.VoteEvent; // Changed package name
import com.example.TRUEVote.service.NotificationService; // Changed package name
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VoteEventConsumer {

    @Autowired
    private NotificationService notificationService;

    @KafkaListener(topics = "${kafka.topic.vote-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeVoteEvent(VoteEvent voteEvent) {
        log.info("Received vote event: {}", voteEvent);
        notificationService.processVoteEvent(voteEvent);
    }
}