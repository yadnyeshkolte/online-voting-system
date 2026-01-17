package com.project.onlinevotingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "election_results")
public class ElectionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    @Column(name = "vote_count")
    private Long voteCount;

    @Column(name = "vote_percentage", precision = 5, scale = 2)
    private BigDecimal votePercentage;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }

    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public Candidate getCandidate() { return candidate; }
    public void setCandidate(Candidate candidate) { this.candidate = candidate; }

    public Long getVoteCount() { return voteCount; }
    public void setVoteCount(Long voteCount) { this.voteCount = voteCount; }

    public BigDecimal getVotePercentage() { return votePercentage; }
    public void setVotePercentage(BigDecimal votePercentage) { this.votePercentage = votePercentage; }

    public Integer getRankPosition() { return rankPosition; }
    public void setRankPosition(Integer rankPosition) { this.rankPosition = rankPosition; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
