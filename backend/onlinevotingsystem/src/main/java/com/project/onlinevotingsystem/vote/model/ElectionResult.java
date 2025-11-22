package com.project.onlinevotingsystem.vote.model;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.election.model.Election;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "election_results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"election_id", "candidate_id"})
})
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
    private Long voteCount = 0L;

    @Column(name = "vote_percentage", precision = 5, scale = 2)
    private BigDecimal votePercentage;

    @Column(name = "rank_position")
    private Integer rankPosition;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
