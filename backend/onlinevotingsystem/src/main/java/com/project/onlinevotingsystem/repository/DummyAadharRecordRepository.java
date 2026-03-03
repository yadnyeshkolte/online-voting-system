package com.project.onlinevotingsystem.repository;

import com.project.onlinevotingsystem.entity.DummyAadharRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DummyAadharRecordRepository extends JpaRepository<DummyAadharRecord, Long> {
    Optional<DummyAadharRecord> findByAadharNumberAndFullNameAndDateOfBirth(String aadharNumber, String fullName, LocalDate dateOfBirth);
    Optional<DummyAadharRecord> findByAadharNumber(String aadharNumber);
}
