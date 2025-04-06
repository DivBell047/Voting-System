package com.example.controller;

import com.example.dto.VoteCount;
import com.example.entity.Vote;
import com.example.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/registration")
@Tag(name = "Vote", description = "Endpoints for vote registration and vote counting")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/register")
    @Operation(summary = "Register a vote", description = "Registers a vote for a user and candidate.")
    @ApiResponse(responseCode = "201", description = "Vote registered successfully", content = @Content(schema = @Schema(implementation = Vote.class)))
    @ApiResponse(responseCode = "400", description = "Bad request (missing IDs or user already voted)", content = @Content(schema = @Schema(type = "string")))
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
    @Operation(summary = "Check if a user has voted", description = "Checks if a user has already voted and returns vote details if they have.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = Map.class)))
    public ResponseEntity<Map<String, Object>> checkUserVote(
            @Parameter(description = "User ID to check", required = true) @PathVariable Long userId) {
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
    @Operation(summary = "Get vote counts by candidate", description = "Returns a list of vote counts grouped by candidate.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteCount.class)))
    public List<VoteCount> getVoteCountsByCandidate() {
        return voteService.getVoteCountsByCandidate();
    }
}