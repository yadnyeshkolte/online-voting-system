package com.project.onlinevotingsystem.vote.repository;

import com.project.onlinevotingsystem.vote.model.ElectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ElectionResultRepository extends JpaRepository<ElectionResult, Long> {
    List<ElectionResult> findByElection_ElectionIdOrderByVoteCountDesc(Long electionId);
    Optional<ElectionResult> findByElection_ElectionIdAndCandidate_CandidateId(Long electionId, Long candidateId);
}
