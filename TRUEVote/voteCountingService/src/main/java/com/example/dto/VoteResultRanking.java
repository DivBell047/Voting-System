package com.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteResultRanking {
    private Long candidateId;
    private Long voteCount;
    private LocalDateTime lastUpdated;
}
