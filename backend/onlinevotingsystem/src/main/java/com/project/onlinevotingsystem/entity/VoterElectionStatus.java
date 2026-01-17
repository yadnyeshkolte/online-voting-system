package com.project.onlinevotingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "voter_election_status", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"election_id", "user_id"})
})
public class VoterElectionStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Long statusId;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "has_voted")
    private Boolean hasVoted = false;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    public Long getStatusId() { return statusId; }
    public void setStatusId(Long statusId) { this.statusId = statusId; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Boolean getHasVoted() { return hasVoted; }
    public void setHasVoted(Boolean hasVoted) { this.hasVoted = hasVoted; }

    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
