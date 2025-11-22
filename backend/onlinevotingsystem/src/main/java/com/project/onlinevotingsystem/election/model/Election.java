package com.project.onlinevotingsystem.election.model;

import com.project.onlinevotingsystem.user.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
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
    @Column(name = "election_type", nullable = false)
    private ElectionType electionType;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private ElectionStatus status = ElectionStatus.DRAFT;

    @Column(name = "result_published")
    private boolean resultPublished = false;

    @Column(name = "result_published_at")
    private LocalDateTime resultPublishedAt;

    @ManyToOne
    @JoinColumn(name = "result_published_by")
    private User resultPublishedBy;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
