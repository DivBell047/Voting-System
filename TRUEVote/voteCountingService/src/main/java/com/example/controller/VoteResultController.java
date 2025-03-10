package com.example.controller;

import com.example.dto.VoteResultRanking;
import com.example.entity.VoteResult;
import com.example.service.VoteResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counting")
public class VoteResultController {
    @Autowired
    private VoteResultService voteResultService;

    @PostMapping("/update")
    public ResponseEntity<String> updateVoteCounts() {
        voteResultService.updateAllVoteCounts();
        return ResponseEntity.ok("Vote counts updated successfully");
    }

    @GetMapping("/rankings")
    public ResponseEntity<List<VoteResultRanking>> getCandidateRankings() {
        List<VoteResultRanking> rankings = voteResultService.getCandidateRankings();
        return ResponseEntity.ok(rankings);
    }
}