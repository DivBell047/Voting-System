package com.example.controller;

import com.example.dto.VoteResultRanking;
import com.example.entity.VoteResult;
import com.example.service.VoteResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/counting")
@Tag(name = "Vote Result", description = "Endpoints for vote counting and rankings")
public class VoteResultController {

    @Autowired
    private VoteResultService voteResultService;

    @PostMapping("/update")
    @Operation(summary = "Update vote counts", description = "Updates all vote counts based on the current votes.")
    @ApiResponse(responseCode = "200", description = "Vote counts updated successfully", content = @Content(schema = @Schema(type = "string")))
    public ResponseEntity<String> updateVoteCounts() {
        voteResultService.updateAllVoteCounts();
        return ResponseEntity.ok("Vote counts updated successfully");
    }

    @GetMapping("/rankings")
    @Operation(summary = "Get candidate rankings", description = "Retrieves the candidate rankings based on vote counts.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = VoteResultRanking.class)))
    public ResponseEntity<List<VoteResultRanking>> getCandidateRankings() {
        List<VoteResultRanking> rankings = voteResultService.getCandidateRankings();
        return ResponseEntity.ok(rankings);
    }
}