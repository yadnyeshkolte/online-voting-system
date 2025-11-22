package com.project.onlinevotingsystem.vote.service;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.candidate.repository.CandidateRepository;
import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.election.model.ElectionStatus;
import com.project.onlinevotingsystem.election.repository.ElectionRepository;
import com.project.onlinevotingsystem.user.model.User;
import com.project.onlinevotingsystem.user.repository.UserRepository;
import com.project.onlinevotingsystem.vote.model.Vote;
import com.project.onlinevotingsystem.vote.model.VoterElectionStatus;
import com.project.onlinevotingsystem.vote.repository.VoteRepository;
import com.project.onlinevotingsystem.vote.repository.VoterElectionStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class VoteService {

    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private VoterElectionStatusRepository statusRepository;
    @Autowired
    private ElectionRepository electionRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Vote castVote(String userEmail, Long electionId, Long candidateId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        Election election = electionRepository.findById(electionId).orElseThrow(() -> new RuntimeException("Election not found"));

        if (election.getStatus() != ElectionStatus.ACTIVE) {
            throw new RuntimeException("Election is not active");
        }

        // Check if user has already voted
        if (statusRepository.findByElection_ElectionIdAndUser_UserId(electionId, user.getUserId())
                .map(VoterElectionStatus::isHasVoted).orElse(false)) {
            throw new RuntimeException("User has already voted in this election");
        }

        // Also check votes table just in case
        if (voteRepository.findByElection_ElectionIdAndUser_UserId(electionId, user.getUserId()).isPresent()) {
            throw new RuntimeException("Vote record already exists");
        }

        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow(() -> new RuntimeException("Candidate not found"));

        if (!candidate.getElection().getElectionId().equals(electionId)) {
            throw new RuntimeException("Candidate does not belong to this election");
        }

        // Create Vote
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setUser(user);
        vote.setCandidate(candidate);
        vote.setVoteHash(UUID.randomUUID().toString()); // Simple hash for now

        Vote savedVote = voteRepository.save(vote);

        // Update Status
        VoterElectionStatus status = statusRepository.findByElection_ElectionIdAndUser_UserId(electionId, user.getUserId())
                .orElse(new VoterElectionStatus());
        status.setElection(election);
        status.setUser(user);
        status.setHasVoted(true);
        status.setVotedAt(LocalDateTime.now());
        statusRepository.save(status);

        return savedVote;
    }

    public boolean hasVoted(String userEmail, Long electionId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
         return statusRepository.findByElection_ElectionIdAndUser_UserId(electionId, user.getUserId())
                .map(VoterElectionStatus::isHasVoted).orElse(false);
    }
}
