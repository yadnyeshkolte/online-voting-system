package com.project.onlinevotingsystem.repository;

import com.project.onlinevotingsystem.entity.DummyVoterIdRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DummyVoterIdRecordRepository extends JpaRepository<DummyVoterIdRecord, Long> {
    Optional<DummyVoterIdRecord> findByVoterIdNumberAndFullNameAndDateOfBirth(String voterIdNumber, String fullName, LocalDate dateOfBirth);
    Optional<DummyVoterIdRecord> findByVoterIdNumber(String voterIdNumber);
}
