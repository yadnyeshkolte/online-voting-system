package com.project.onlinevotingsystem.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "elections")
public class Election {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "election_id")
    private Long electionId;

    @Column(name = "election_name", nullable = false)
    private String electionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "election_type")
    private ElectionType electionType;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ElectionStatus status = ElectionStatus.DRAFT;

    @Column(name = "result_published")
    private Boolean resultPublished = false;

    @Column(name = "result_published_at")
    private LocalDateTime resultPublishedAt;

    @Column(name = "result_published_by")
    private Long resultPublishedBy;

    @Column(name = "created_by")
    private Long createdBy;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Candidate> candidates;

    public Long getElectionId() { return electionId; }
    public void setElectionId(Long electionId) { this.electionId = electionId; }

    public String getElectionName() { return electionName; }
    public void setElectionName(String electionName) { this.electionName = electionName; }

    public ElectionType getElectionType() { return electionType; }
    public void setElectionType(ElectionType electionType) { this.electionType = electionType; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public ElectionStatus getStatus() { return status; }
    public void setStatus(ElectionStatus status) { this.status = status; }

    public Boolean getResultPublished() { return resultPublished; }
    public void setResultPublished(Boolean resultPublished) { this.resultPublished = resultPublished; }

    public LocalDateTime getResultPublishedAt() { return resultPublishedAt; }
    public void setResultPublishedAt(LocalDateTime resultPublishedAt) { this.resultPublishedAt = resultPublishedAt; }

    public Long getResultPublishedBy() { return resultPublishedBy; }
    public void setResultPublishedBy(Long resultPublishedBy) { this.resultPublishedBy = resultPublishedBy; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public List<Candidate> getCandidates() { return candidates; }
    public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }
}
