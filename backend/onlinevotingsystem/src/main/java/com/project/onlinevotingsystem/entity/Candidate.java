package com.project.onlinevotingsystem.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidates", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "election_id"})
})
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "candidate_id")
    private Long candidateId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "election_id", nullable = false)
    @JsonBackReference
    private Election election;

    @Column(name = "party_name")
    private String partyName;

    @Column(name = "party_symbol")
    private String partySymbol;

    @Lob
    @Column(name = "candidate_photo", columnDefinition = "LONGBLOB")
    private byte[] candidatePhoto;

    @Lob
    @Column(name = "manifesto", columnDefinition = "TEXT")
    private String manifesto;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getCandidateId() { return candidateId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }

    public String getPartyName() { return partyName; }
    public void setPartyName(String partyName) { this.partyName = partyName; }

    public String getPartySymbol() { return partySymbol; }
    public void setPartySymbol(String partySymbol) { this.partySymbol = partySymbol; }

    public byte[] getCandidatePhoto() { return candidatePhoto; }
    public void setCandidatePhoto(byte[] candidatePhoto) { this.candidatePhoto = candidatePhoto; }

    public String getManifesto() { return manifesto; }
    public void setManifesto(String manifesto) { this.manifesto = manifesto; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
