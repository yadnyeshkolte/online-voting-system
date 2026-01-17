package com.project.onlinevotingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "election_reports")
public class ElectionReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Long reportId;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Column(name = "total_registered_voters")
    private Long totalRegisteredVoters;

    @Column(name = "total_votes_cast")
    private Long totalVotesCast;

    @Column(name = "voter_turnout_percentage", precision = 5, scale = 2)
    private BigDecimal voterTurnoutPercentage;

    @Column(name = "total_candidates")
    private Integer totalCandidates;

    @ManyToOne
    @JoinColumn(name = "winning_candidate_id")
    private Candidate winningCandidate;

    @Column(name = "winning_margin")
    private Long winningMargin;

    @Column(name = "report_generated_by")
    private Long reportGeneratedBy;

    @Column(name = "report_generated_at")
    private LocalDateTime reportGeneratedAt;

    @PrePersist
    protected void onCreate() {
        reportGeneratedAt = LocalDateTime.now();
    }

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public Long getTotalRegisteredVoters() { return totalRegisteredVoters; }
    public void setTotalRegisteredVoters(Long totalRegisteredVoters) { this.totalRegisteredVoters = totalRegisteredVoters; }

    public Long getTotalVotesCast() { return totalVotesCast; }
    public void setTotalVotesCast(Long totalVotesCast) { this.totalVotesCast = totalVotesCast; }

    public BigDecimal getVoterTurnoutPercentage() { return voterTurnoutPercentage; }
    public void setVoterTurnoutPercentage(BigDecimal voterTurnoutPercentage) { this.voterTurnoutPercentage = voterTurnoutPercentage; }

    public Integer getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(Integer totalCandidates) { this.totalCandidates = totalCandidates; }

    public Candidate getWinningCandidate() { return winningCandidate; }
    public void setWinningCandidate(Candidate winningCandidate) { this.winningCandidate = winningCandidate; }

    public Long getWinningMargin() { return winningMargin; }
    public void setWinningMargin(Long winningMargin) { this.winningMargin = winningMargin; }

    public Long getReportGeneratedBy() { return reportGeneratedBy; }
    public void setReportGeneratedBy(Long reportGeneratedBy) { this.reportGeneratedBy = reportGeneratedBy; }

    public LocalDateTime getReportGeneratedAt() { return reportGeneratedAt; }
    public void setReportGeneratedAt(LocalDateTime reportGeneratedAt) { this.reportGeneratedAt = reportGeneratedAt; }
}
