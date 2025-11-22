package com.project.onlinevotingsystem.vote.service;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.candidate.repository.CandidateRepository;
import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.election.repository.ElectionRepository;
import com.project.onlinevotingsystem.vote.model.ElectionResult;
import com.project.onlinevotingsystem.vote.model.Vote;
import com.project.onlinevotingsystem.vote.repository.ElectionResultRepository;
import com.project.onlinevotingsystem.vote.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ResultService {

    @Autowired
    private ElectionResultRepository resultRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private ElectionRepository electionRepository;

    @Transactional
    public List<ElectionResult> calculateResults(Long electionId) {
        Election election = electionRepository.findById(electionId).orElseThrow(() -> new RuntimeException("Election not found"));
        List<Candidate> candidates = candidateRepository.findByElection_ElectionId(electionId);

        long totalVotes = 0;

        // This is not efficient for millions of votes, but okay for a start/small scale
        // Ideally use COUNT queries in repository
        // But to keep it modular and simple:

        // Clear previous results or Update? Better update or recalculate.
        // Let's iterate candidates and count votes.

        for (Candidate candidate : candidates) {
            long count = voteRepository.findAll().stream()
                    .filter(v -> v.getElection().getElectionId().equals(electionId) && v.getCandidate().getCandidateId().equals(candidate.getCandidateId()))
                    .count();
                    // Optimization: add countByElectionAndCandidate to VoteRepository

            ElectionResult result = resultRepository.findByElection_ElectionIdAndCandidate_CandidateId(electionId, candidate.getCandidateId())
                    .orElse(new ElectionResult());

            result.setElection(election);
            result.setCandidate(candidate);
            result.setVoteCount(count);
            resultRepository.save(result);
            totalVotes += count;
        }

        // Calculate Percentages and Ranks
        if (totalVotes > 0) {
            List<ElectionResult> results = resultRepository.findByElection_ElectionIdOrderByVoteCountDesc(electionId);
            int rank = 1;
            for (ElectionResult result : results) {
                 BigDecimal percentage = BigDecimal.valueOf(result.getVoteCount())
                         .multiply(BigDecimal.valueOf(100))
                         .divide(BigDecimal.valueOf(totalVotes), 2, RoundingMode.HALF_UP);
                 result.setVotePercentage(percentage);
                 result.setRankPosition(rank++);
                 resultRepository.save(result);
            }
            return results;
        }

        return resultRepository.findByElection_ElectionIdOrderByVoteCountDesc(electionId);
    }

    public List<ElectionResult> getResults(Long electionId) {
        return resultRepository.findByElection_ElectionIdOrderByVoteCountDesc(electionId);
    }
}
