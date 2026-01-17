package com.project.onlinevotingsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dummy_voter_id_records")
public class DummyVoterIdRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voter_record_id")
    private Long voterRecordId;

    @Column(name = "voter_id_number", unique = true, length = 20)
    private String voterIdNumber;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "is_valid")
    private Boolean isValid = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getVoterRecordId() { return voterRecordId; }
    public void setVoterRecordId(Long voterRecordId) { this.voterRecordId = voterRecordId; }

    public String getVoterIdNumber() { return voterIdNumber; }
    public void setVoterIdNumber(String voterIdNumber) { this.voterIdNumber = voterIdNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
