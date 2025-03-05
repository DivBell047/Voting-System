package com.example.TRUEVote.repository;

import com.example.TRUEVote.model.Notification; // Import the Notification model
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // Add this annotation
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // You can add custom query methods here if needed
}