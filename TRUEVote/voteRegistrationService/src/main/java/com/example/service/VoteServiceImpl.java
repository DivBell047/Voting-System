package com.example.service;

import com.example.dto.VoteCount;
import com.example.entity.Vote;
import com.example.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteServiceImpl implements VoteService {

    @Autowired
    private VoteRepository voteRepository;

    @Override
    @Transactional
    public Vote registerVote(Vote vote) {
        // Ensure we have both user ID and candidate ID
        if (vote.getUserId() == null || vote.getCandidateId() == null) {
            throw new IllegalArgumentException("User ID and Candidate ID are required");
        }

        // Check if user has already voted
        if (hasUserVoted(vote.getUserId())) {
            throw new IllegalStateException("User has already voted");
        }

        // Set the current time for votedAt if it's not already set
        if (vote.getVotedAt() == null) {
            vote.setVotedAt(LocalDateTime.now());
        }

        try {
            // Save the vote
            return voteRepository.save(vote);
        } catch (DataIntegrityViolationException e) {
            // This handles the case where the unique constraint is violated
            throw new IllegalStateException("User has already voted for this candidate", e);
        }
    }

    @Override
    public boolean hasUserVoted(Long userId) {
        return voteRepository.existsByUserId(userId);
    }

    @Override
    public Vote getUserVote(Long userId) {
        Optional<Vote> vote = voteRepository.findByUserId(userId);
        return vote.orElse(null);
    }

    @Override
    public List<VoteCount> getVoteCountsByCandidate() {
        return voteRepository.countVotesByCandidateId().stream()
                .map(result -> new VoteCount(
                        (Long) result[0],
                        Long.parseLong(result[1].toString())
                ))
                .collect(Collectors.toList());
    }
}
