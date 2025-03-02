package com.example.candidate.service;

import com.example.candidate.entity.Candidate;
import com.example.candidate.repo.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public List<Candidate> getAllCandidates() {
        return candidateRepository.findAll();
    }

    public Candidate getCandidateById(String id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + id));
    }

    public Candidate addCandidate(Candidate candidate) {
        candidate.setId(null); // Ensure it's treated as a new entity
        return candidateRepository.save(candidate);
    }

    public Candidate updateCandidate(String id, Candidate candidate) {
        Candidate existingCandidate = candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + id));

        candidate.setId(id); // Ensure the ID is set for the update
        return candidateRepository.save(candidate);
    }

    public void deleteCandidate(String id) {
        if (!candidateRepository.existsById(id)) {
            throw new EntityNotFoundException("Candidate not found with id: " + id);
        }
        candidateRepository.deleteById(id);
    }
}