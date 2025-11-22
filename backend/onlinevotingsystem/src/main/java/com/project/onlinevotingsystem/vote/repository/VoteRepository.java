package com.project.onlinevotingsystem.vote.repository;

import com.project.onlinevotingsystem.vote.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findByElection_ElectionIdAndUser_UserId(Long electionId, Long userId);
}
