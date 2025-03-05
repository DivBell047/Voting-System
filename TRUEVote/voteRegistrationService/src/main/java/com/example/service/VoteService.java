package com.example.service;

import com.example.dto.VoteCount;
import com.example.entity.Vote;

import java.util.List;

public interface VoteService {
    Vote registerVote(Vote vote);
    boolean hasUserVoted(Long userId);
    Vote getUserVote(Long userId);
    List<VoteCount> getVoteCountsByCandidate();
}
