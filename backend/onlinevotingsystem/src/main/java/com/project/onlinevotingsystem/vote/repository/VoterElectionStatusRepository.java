package com.project.onlinevotingsystem.vote.repository;

import com.project.onlinevotingsystem.vote.model.VoterElectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoterElectionStatusRepository extends JpaRepository<VoterElectionStatus, Long> {
    Optional<VoterElectionStatus> findByElection_ElectionIdAndUser_UserId(Long electionId, Long userId);
}
