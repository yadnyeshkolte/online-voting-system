package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.entity.*;
import com.project.onlinevotingsystem.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterElectionStatusRepository voterElectionStatusRepository;
    private final ElectionRepository electionRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;

    public VoteService(VoteRepository voteRepository, VoterElectionStatusRepository voterElectionStatusRepository, ElectionRepository electionRepository, UserRepository userRepository, CandidateRepository candidateRepository, ElectionService electionService) {
        this.voteRepository = voteRepository;
        this.voterElectionStatusRepository = voterElectionStatusRepository;
        this.electionRepository = electionRepository;
        this.userRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.electionService = electionService;
    }

    @Transactional
    public Vote castVote(Long electionId, Long userId, Long candidateId) {
        // Check if election is active
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (election.getStatus() != ElectionStatus.ACTIVE) {
            throw new RuntimeException("Election is not active");
        }

        // Check if user has already voted
        Optional<VoterElectionStatus> existingStatusOpt = voterElectionStatusRepository.findByElection_ElectionIdAndUser_UserId(electionId, userId);
        
        if (existingStatusOpt.isPresent() && Boolean.TRUE.equals(existingStatusOpt.get().getHasVoted())) {
            throw new RuntimeException("User has already voted in this election");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Create Vote
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setUser(user);
        vote.setCandidate(candidate);
        vote.setVoteHash(UUID.randomUUID().toString());
        vote = voteRepository.save(vote);

        // Update Status
        VoterElectionStatus status;
        if (existingStatusOpt.isPresent()) {
            status = existingStatusOpt.get();
        } else {
            status = new VoterElectionStatus();
            status.setElection(election);
            status.setUser(user);
        }
        status.setHasVoted(true);
        status.setVotedAt(LocalDateTime.now());
        voterElectionStatusRepository.save(status);

        // Update Election Results
        electionService.calculateResults(electionId);

        return vote;
    }

    public boolean hasUserVoted(Long electionId, Long userId) {
        return voterElectionStatusRepository.findByElection_ElectionIdAndUser_UserId(electionId, userId)
                .map(status -> Boolean.TRUE.equals(status.getHasVoted()))
                .orElse(false);
    }
}
