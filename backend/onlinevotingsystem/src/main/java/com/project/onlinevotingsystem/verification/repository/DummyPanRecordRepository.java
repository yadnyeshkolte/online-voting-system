package com.project.onlinevotingsystem.verification.repository;

import com.project.onlinevotingsystem.verification.model.DummyPanRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DummyPanRecordRepository extends JpaRepository<DummyPanRecord, Long> {
    Optional<DummyPanRecord> findByPanNumber(String panNumber);
}
