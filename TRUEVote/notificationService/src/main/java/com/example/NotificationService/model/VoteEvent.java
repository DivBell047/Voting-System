package com.example.NotificationService.model;

import java.util.Objects;

public class VoteEvent {
    private String candidateName;
    private Long candidateId;
    private String userId;

    // No-argument constructor (needed for JSON deserialization)
    public VoteEvent() {
    }

    // All-arguments constructor
    public VoteEvent(String candidateName, Long candidateId, String userId) {
        this.candidateName = candidateName;
        this.candidateId = candidateId;
        this.userId = userId;
    }

    // Getters
    public String getCandidateName() {
        return candidateName;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getUserId() {
        return userId;
    }

    // Setters
    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public void setCandidateId(Long candidateId) {
        this.candidateId = candidateId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    // equals()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VoteEvent voteEvent = (VoteEvent) o;
        return Objects.equals(candidateName, voteEvent.candidateName) &&
                Objects.equals(candidateId, voteEvent.candidateId) &&
                Objects.equals(userId, voteEvent.userId);
    }

    // hashCode()
    @Override
    public int hashCode() {
        return Objects.hash(candidateName, candidateId, userId);
    }

    // toString()
    @Override
    public String toString() {
        return "VoteEvent{" +
                "candidateName='" + candidateName + '\'' +
                ", candidateId=" + candidateId +
                ", userId='" + userId + '\'' +
                '}';
    }
}