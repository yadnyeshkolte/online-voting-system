package com.project.onlinevotingsystem.verification.repository;

import com.project.onlinevotingsystem.verification.model.DummyPassportRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DummyPassportRecordRepository extends JpaRepository<DummyPassportRecord, Long> {
    Optional<DummyPassportRecord> findByPassportNumber(String passportNumber);
}
