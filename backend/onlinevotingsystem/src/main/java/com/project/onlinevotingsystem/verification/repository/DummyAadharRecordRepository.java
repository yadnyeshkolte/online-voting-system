package com.project.onlinevotingsystem.verification.repository;

import com.project.onlinevotingsystem.verification.model.DummyAadharRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DummyAadharRecordRepository extends JpaRepository<DummyAadharRecord, Long> {
    Optional<DummyAadharRecord> findByAadharNumber(String aadharNumber);
}
