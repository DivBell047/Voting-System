package com.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vote_results")
public class VoteResult {
    @Id
    @Column(name = "candidate_id")
    private Long candidateId;

    @Column(name = "vote_count", nullable = false)
    private Long voteCount;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}
