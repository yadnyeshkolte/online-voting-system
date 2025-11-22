package com.project.onlinevotingsystem.candidate.repository;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByElection_ElectionId(Long electionId);
}
