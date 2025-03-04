package com.example.candidate.service;

import com.example.candidate.dto.CandidateCreateDTO;
import com.example.candidate.dto.CandidateDTO;
import com.example.candidate.dto.CandidateUpdateDTO;
import com.example.candidate.entity.Candidate;
import com.example.candidate.repo.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;

    @Autowired
    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    public List<CandidateDTO> getAllCandidates() {
        List<Candidate> candidates = candidateRepository.findAll();
        return candidates.stream()
                .map(this::convertToDto)  // Use a method to convert
                .collect(Collectors.toList());
    }

    public CandidateDTO getCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + id));
        return convertToDto(candidate); // Use a method to convert
    }

    public CandidateDTO addCandidate(CandidateCreateDTO candidateCreateDTO) {
        Candidate candidate = convertToEntity(candidateCreateDTO); // Use a method to convert
        candidate.setId(null); // Ensure it's treated as a new entity
        Candidate savedCandidate = candidateRepository.save(candidate);
        return convertToDto(savedCandidate); // Use a method to convert
    }

    public CandidateDTO updateCandidate(Long id, CandidateUpdateDTO candidateUpdateDTO) {
        Candidate existingCandidate = candidateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Candidate not found with id: " + id));

        // Manually copy the data from the DTO to the existing entity
        if (candidateUpdateDTO.getName() != null) {
            existingCandidate.setName(candidateUpdateDTO.getName());
        }
        if (candidateUpdateDTO.getParty() != null) {
            existingCandidate.setParty(candidateUpdateDTO.getParty());
        }
        if (candidateUpdateDTO.getPosition() != null) {
            existingCandidate.setPosition(candidateUpdateDTO.getPosition());
        }
        if (candidateUpdateDTO.getDescription() != null) {
            existingCandidate.setDescription(candidateUpdateDTO.getDescription());
        }
        if (candidateUpdateDTO.getImageUrl() != null) {
            existingCandidate.setImageUrl(candidateUpdateDTO.getImageUrl());
        }

        Candidate updatedCandidate = candidateRepository.save(existingCandidate);
        return convertToDto(updatedCandidate); // Use a method to convert
    }

    public void deleteCandidate(Long id) {
        if (!candidateRepository.existsById(id)) {
            throw new EntityNotFoundException("Candidate not found with id: " + id);
        }
        candidateRepository.deleteById(id);
    }

    // Helper method to convert from Entity to DTO
    private CandidateDTO convertToDto(Candidate candidate) {
        CandidateDTO candidateDTO = new CandidateDTO();
        candidateDTO.setId(candidate.getId());
        candidateDTO.setName(candidate.getName());
        candidateDTO.setParty(candidate.getParty());
        candidateDTO.setPosition(candidate.getPosition());
        candidateDTO.setDescription(candidate.getDescription());
        candidateDTO.setImageUrl(candidate.getImageUrl());
        return candidateDTO;
    }

    // Helper method to convert from CreateDTO to Entity
    private Candidate convertToEntity(CandidateCreateDTO candidateCreateDTO) {
        Candidate candidate = new Candidate();
        candidate.setName(candidateCreateDTO.getName());
        candidate.setParty(candidateCreateDTO.getParty());
        candidate.setPosition(candidateCreateDTO.getPosition());
        candidate.setDescription(candidateCreateDTO.getDescription());
        candidate.setImageUrl(candidateCreateDTO.getImageUrl());
        return candidate;
    }
}