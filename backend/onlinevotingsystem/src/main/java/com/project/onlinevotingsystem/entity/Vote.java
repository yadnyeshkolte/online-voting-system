package com.project.onlinevotingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"election_id", "user_id"})
})
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vote_id")
    private Long voteId;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "vote_hash", unique = true)
    private String voteHash;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;

    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }

    public Long getVoteId() { return voteId; }
    public void setVoteId(Long voteId) { this.voteId = voteId; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public String getVoteHash() { return voteHash; }
    public void setVoteHash(String voteHash) { this.voteHash = voteHash; }

    public LocalDateTime getVotedAt() { return votedAt; }
    public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
}
