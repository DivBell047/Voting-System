package com.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(name = "unique_vote", columnNames = {"user_id", "candidate_id"})
})
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
}