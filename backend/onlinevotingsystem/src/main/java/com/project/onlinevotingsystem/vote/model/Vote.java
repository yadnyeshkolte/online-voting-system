package com.project.onlinevotingsystem.vote.model;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
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

    @Column(name = "vote_hash", nullable = false, unique = true)
    private String voteHash;

    @CreationTimestamp
    @Column(name = "voted_at")
    private LocalDateTime votedAt;
}
