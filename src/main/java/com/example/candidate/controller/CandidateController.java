package com.example.candidate.controller;

import com.example.candidate.dto.CandidateCreateDTO;
import com.example.candidate.dto.CandidateDTO;
import com.example.candidate.dto.CandidateUpdateDTO;
import com.example.candidate.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidates") // This is now relative to /api/v1
public class CandidateController {

    private final CandidateService candidateService;

    @Autowired
    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping
    public ResponseEntity<List<CandidateDTO>> getAllCandidates() {
        List<CandidateDTO> candidates = candidateService.getAllCandidates();
        return new ResponseEntity<>(candidates, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CandidateDTO> getCandidateById(@PathVariable Long id) {
        try {
            CandidateDTO candidate = candidateService.getCandidateById(id);
            return new ResponseEntity<>(candidate, HttpStatus.OK);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<CandidateDTO> addCandidate(@Valid @RequestBody CandidateCreateDTO candidateCreateDTO) {
        CandidateDTO savedCandidate = candidateService.addCandidate(candidateCreateDTO);
        return new ResponseEntity<>(savedCandidate, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CandidateDTO> updateCandidate(@PathVariable Long id, @Valid @RequestBody CandidateUpdateDTO candidateUpdateDTO) {
        try {
            CandidateDTO updatedCandidate = candidateService.updateCandidate(id, candidateUpdateDTO);
            return new ResponseEntity<>(updatedCandidate, HttpStatus.OK);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        try {
            candidateService.deleteCandidate(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}