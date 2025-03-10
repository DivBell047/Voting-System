package com.example.NotificationService.repository;

import com.example.NotificationService.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository  // Add this annotation
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // You can add custom query methods here if needed
}