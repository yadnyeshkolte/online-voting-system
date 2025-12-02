package com.project.onlinevotingsystem.repository;

import com.project.onlinevotingsystem.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByElection_ElectionId(Long electionId);
    List<Candidate> findByElection_ElectionIdOrderByCandidateIdAsc(Long electionId);
}
