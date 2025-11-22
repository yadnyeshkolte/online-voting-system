package com.project.onlinevotingsystem.vote.repository;

import com.project.onlinevotingsystem.vote.model.ElectionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ElectionReportRepository extends JpaRepository<ElectionReport, Long> {
    Optional<ElectionReport> findByElection_ElectionId(Long electionId);
}
