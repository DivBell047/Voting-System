package com.example.service;

import com.example.dto.VoteCount;
import com.example.entity.Vote;
import com.example.exception.CandidateNotFoundException;
import com.example.exception.ServiceCommunicationException;
import com.example.exception.UserNotFoundException;
import com.example.repository.VoteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteServiceImpl implements VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteServiceImpl.class);

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private RestTemplate restTemplate; // Still the plain RestTemplate

    // Inject Gateway URL and route prefixes ONLY
    @Value("${api-gateway.url}")
    private String apiGatewayUrl;

    @Value("${app.gateway.routes.user-management}") // Should resolve to /user
    private String userManagementRoutePrefix;

    @Value("${app.gateway.routes.candidate-service}") // Should resolve to /candidates
    private String candidateServiceRoutePrefix;

    // REMOVED injection of internal path properties

    @Override
    @Transactional
    public Vote registerVote(Vote vote) {
        Long userId = vote.getUserId();
        Long candidateId = vote.getCandidateId();

        if (userId == null || candidateId == null) {
            throw new IllegalArgumentException("User ID and Candidate ID are required");
        }

        // --- START VALIDATION via API Gateway ---
        if (!checkUserExists(userId)) {
            log.warn("Attempted to register vote for non-existent user ID: {}", userId);
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        if (!checkCandidateExists(candidateId)) {
            log.warn("Attempted to register vote for non-existent candidate ID: {}", candidateId);
            throw new CandidateNotFoundException("Candidate with ID " + candidateId + " not found.");
        }
        // --- END VALIDATION ---

        if (hasUserVoted(userId)) {
            log.warn("User {} has already voted.", userId);
            throw new IllegalStateException("User has already voted");
        }

        if (vote.getVotedAt() == null) {
            vote.setVotedAt(LocalDateTime.now());
        }

        try {
            log.info("Registering vote for user {} for candidate {}", userId, candidateId);
            Vote savedVote = voteRepository.save(vote);
            log.info("Vote registered successfully with ID: {}", savedVote.getId());
            // TODO: Add communication with voteCountingService here if needed (e.g., apiGatewayUrl + "/counting/...")
            return savedVote;
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation for user {} and candidate {}. Likely already voted.", userId, candidateId, e);
            throw new IllegalStateException("User has already voted for this candidate", e);
        } catch (Exception e) {
            log.error("Failed to save vote for user {} and candidate {}", userId, candidateId, e);
            throw new ServiceCommunicationException("Failed to save vote due to an internal error.");
        }
    }

    private boolean checkUserExists(Long userId) {
        // Build the URL: ${api-gateway.url}/${user-route-prefix}/{userId}
        // Example: http://localhost:9090/user/123
        // **ASSUMPTION**: The userManagement service expects requests at /{id} relative to the gateway path /user/**
        String url = UriComponentsBuilder.fromHttpUrl(apiGatewayUrl)
                .path(userManagementRoutePrefix) // Adds /user
                .pathSegment("check")
                .pathSegment(String.valueOf(userId)) // Adds /{userId}
                .toUriString();

        log.debug("Checking user existence via Gateway at URL: {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, null, Object.class);
            boolean exists = response.getStatusCode().is2xxSuccessful();
            log.debug("User check for ID {} returned status {} (Exists: {})", userId, response.getStatusCode(), exists);
            return exists;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("User not found via Gateway check at {}: {}", url, ex.getMessage());
            return false;
        } catch (RestClientException ex) {
            log.error("Error checking user existence for user {} via Gateway at {}: {}", userId, url, ex.getMessage());
            throw new ServiceCommunicationException("Failed to communicate via API Gateway to validate user " + userId, ex);
        }
    }

    private boolean checkCandidateExists(Long candidateId) {
        // Build the URL: ${api-gateway.url}/${candidate-route-prefix}/{candidateId}
        // Example: http://localhost:9090/candidates/45
        // This matches your gateway route /candidates/** and candidate service mapping /candidates/{id}
        String url = UriComponentsBuilder.fromHttpUrl(apiGatewayUrl)
                .path(candidateServiceRoutePrefix) // Adds /candidates
                .pathSegment(String.valueOf(candidateId)) // Adds /{candidateId}
                .toUriString();

        log.debug("Checking candidate existence via Gateway at URL: {}", url);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.GET, null, Object.class);
            boolean exists = response.getStatusCode().is2xxSuccessful();
            log.debug("Candidate check for ID {} returned status {} (Exists: {})", candidateId, response.getStatusCode(), exists);
            return exists;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Candidate not found via Gateway check at {}: {}", url, ex.getMessage());
            return false;
        } catch (RestClientException ex) {
            log.error("Error checking candidate existence for candidate {} via Gateway at {}: {}", candidateId, url, ex.getMessage());
            throw new ServiceCommunicationException("Failed to communicate via API Gateway to validate candidate " + candidateId, ex);
        }
    }

    // --- Other existing methods remain the same ---
    @Override
    public boolean hasUserVoted(Long userId) {
        return voteRepository.existsByUserId(userId);
    }

    @Override
    public Vote getUserVote(Long userId) {
        return voteRepository.findByUserId(userId).orElse(null);
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