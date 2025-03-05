package com.example.repository;

import com.example.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteResultRepository extends JpaRepository<VoteResult, Long> {
    VoteResult findByCandidateId(Long candidateId);
}