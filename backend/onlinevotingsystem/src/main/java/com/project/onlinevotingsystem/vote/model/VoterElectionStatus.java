package com.project.onlinevotingsystem.vote.model;

import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
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
    private boolean hasVoted = false;

    @Column(name = "voted_at")
    private LocalDateTime votedAt;
}
