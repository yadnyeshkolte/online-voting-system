package com.project.onlinevotingsystem.candidate.model;

import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.user.model.User;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "candidates")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long candidateId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Column(name = "party_name")
    private String partyName;

    @Column(name = "candidate_symbol")
    private String candidateSymbol;

    @Column(name = "candidate_photo_url")
    private String candidatePhotoUrl;
}
