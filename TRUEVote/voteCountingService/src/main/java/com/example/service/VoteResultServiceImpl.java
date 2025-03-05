package com.example.service;

import com.example.dto.VoteCount;
import com.example.dto.VoteResultRanking;
import com.example.entity.VoteResult;
import com.example.repository.VoteResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteResultServiceImpl implements VoteResultService {
    @Autowired
    private VoteResultRepository voteResultRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${vote-registration.service.url}")
    private String voteRegistrationServiceUrl;

    @Override
    @Transactional
    public void updateAllVoteCounts() {
        // Fetch vote counts from vote registration service
        VoteCount[] voteCounts = restTemplate.getForObject(
                voteRegistrationServiceUrl + "/api/votes/count",
                VoteCount[].class
        );

        // Update or create vote Result for each candidate
        LocalDateTime now = LocalDateTime.now();
        assert voteCounts != null;
        for (VoteCount voteCount : voteCounts) {
            VoteResult voteResult = voteResultRepository.findByCandidateId(voteCount.getCandidateId());

            if (voteResult == null) {
                voteResult = new VoteResult();
                voteResult.setCandidateId(voteCount.getCandidateId());
            }

            voteResult.setVoteCount(voteCount.getVoteCount());
            voteResult.setLastUpdated(now);

            voteResultRepository.save(voteResult);
        }
    }

    @Override
    public List<VoteResultRanking> getCandidateRankings() {
        // Fetch all vote Result and sort by vote count in descending order
        return voteResultRepository.findAll().stream()
                .map(result -> new VoteResultRanking(
                        result.getCandidateId(),
                        result.getVoteCount(),
                        result.getLastUpdated()
                ))
                .sorted((a, b) -> b.getVoteCount().compareTo(a.getVoteCount()))
                .collect(Collectors.toList());
    }
}