package com.example.repository;

import com.example.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    // These method names will be automatically implemented by Spring Data JPA
    boolean existsByUserId(Long userId);
    Optional<Vote> findByUserId(Long userId);
    @Query("SELECT v.candidateId, COUNT(v) FROM Vote v GROUP BY v.candidateId")
    List<Object[]> countVotesByCandidateId();
}
