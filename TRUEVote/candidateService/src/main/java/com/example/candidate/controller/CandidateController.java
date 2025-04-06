package com.example.candidate.controller;

import com.example.candidate.dto.CandidateCreateDTO;
import com.example.candidate.dto.CandidateDTO;
import com.example.candidate.dto.CandidateUpdateDTO;
import com.example.candidate.service.CandidateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates")
@Tag(name = "Candidate", description = "Endpoints for managing candidates")
public class CandidateController {

    private final CandidateService candidateService;

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    @Operation(summary = "Get all candidates", description = "Retrieves a list of all candidates.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CandidateDTO.class)))
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<CandidateDTO> candidates = candidateService.getAllCandidates();
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get candidate by ID", description = "Retrieves a candidate by their ID.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CandidateDTO.class)))
    @ApiResponse(responseCode = "404", description = "Candidate not found")
    public ResponseEntity<CandidateDTO> getCandidateById(
            @Parameter(description = "ID of the candidate to retrieve", required = true) @PathVariable Long id) {
        try {
            CandidateDTO candidate = candidateService.getCandidateById(id);
            return new ResponseEntity<>(candidate, HttpStatus.OK);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    @Operation(summary = "Add a new candidate", description = "Adds a new candidate.")
    @ApiResponse(responseCode = "201", description = "Candidate created", content = @Content(schema = @Schema(implementation = CandidateDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<CandidateDTO> addCandidate(@Valid @RequestBody CandidateCreateDTO candidateCreateDTO) {
        CandidateDTO savedCandidate = candidateService.addCandidate(candidateCreateDTO);
        return new ResponseEntity<>(savedCandidate, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a candidate", description = "Updates an existing candidate.")
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = CandidateDTO.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input")
    @ApiResponse(responseCode = "404", description = "Candidate not found")
    public ResponseEntity<CandidateDTO> updateCandidate(
            @Parameter(description = "ID of the candidate to update", required = true) @PathVariable Long id,
            @Valid @RequestBody CandidateUpdateDTO candidateUpdateDTO) {
        try {
            CandidateDTO updatedCandidate = candidateService.updateCandidate(id, candidateUpdateDTO);
            return new ResponseEntity<>(updatedCandidate, HttpStatus.OK);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a candidate", description = "Deletes a candidate by their ID.")
    @ApiResponse(responseCode = "204", description = "No content")
    @ApiResponse(responseCode = "404", description = "Candidate not found")
    public ResponseEntity<Void> deleteCandidate(
            @Parameter(description = "ID of the candidate to delete", required = true) @PathVariable Long id) {
        try {
            candidateService.deleteCandidate(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}