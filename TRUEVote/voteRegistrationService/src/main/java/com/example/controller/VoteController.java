package com.example.controller;

import com.example.dto.VoteCount;
import com.example.entity.Vote;
import com.example.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registration")
public class VoteController {

    @Autowired
    private VoteService voteService;


    @PostMapping("/register")
    public ResponseEntity<?> registerVote(@RequestBody Vote vote) {
        Long userId = vote.getUserId();
        Long candidateId = vote.getCandidateId();

        if (userId == null || candidateId == null) {
            return ResponseEntity.badRequest().body("User ID and Candidate ID are required");
        }

        try {
            Vote savedVote = voteService.registerVote(vote);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedVote);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<Map<String, Object>> checkUserVote(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        boolean hasVoted = voteService.hasUserVoted(userId);
        response.put("hasVoted", hasVoted);

        if (hasVoted) {
            Vote vote = voteService.getUserVote(userId);
            if (vote != null) {
                response.put("vote", vote);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public List<VoteCount> getVoteCountsByCandidate() {
        return voteService.getVoteCountsByCandidate();
    }
}
