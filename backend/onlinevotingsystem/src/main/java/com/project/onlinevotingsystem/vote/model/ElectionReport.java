package com.project.onlinevotingsystem.vote.model;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
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
    private Long totalRegisteredVoters = 0L;

    @Column(name = "total_votes_cast")
    private Long totalVotesCast = 0L;

    @Column(name = "voter_turnout_percentage", precision = 5, scale = 2)
    private BigDecimal voterTurnoutPercentage;

    @Column(name = "total_candidates")
    private Integer totalCandidates = 0;

    @ManyToOne
    @JoinColumn(name = "winning_candidate_id")
    private Candidate winningCandidate;

    @Column(name = "winning_margin")
    private Long winningMargin;

    @ManyToOne
    @JoinColumn(name = "report_generated_by", nullable = false)
    private User reportGeneratedBy;

    @CreationTimestamp
    @Column(name = "report_generated_at")
    private LocalDateTime reportGeneratedAt;
}
