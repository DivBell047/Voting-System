package com.example.NotificationService.consumer;

import com.example.NotificationService.model.VoteEvent;
import com.example.NotificationService.service.NotificationService;
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