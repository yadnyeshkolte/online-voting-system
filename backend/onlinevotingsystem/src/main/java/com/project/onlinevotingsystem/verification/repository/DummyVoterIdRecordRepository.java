package com.project.onlinevotingsystem.verification.repository;

import com.project.onlinevotingsystem.verification.model.DummyVoterIdRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DummyVoterIdRecordRepository extends JpaRepository<DummyVoterIdRecord, Long> {
    Optional<DummyVoterIdRecord> findByVoterIdNumber(String voterIdNumber);
}
