package com.example.service;

import com.example.dto.VoteResultRanking;
import com.example.entity.VoteResult;

import java.util.List;

public interface VoteResultService {
    void updateAllVoteCounts();
    List<VoteResultRanking> getCandidateRankings();
}
