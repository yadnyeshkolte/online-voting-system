package com.project.onlinevotingsystem.candidate.service;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.candidate.repository.CandidateRepository;
import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.election.repository.ElectionRepository;
import com.project.onlinevotingsystem.user.model.User;
import com.project.onlinevotingsystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private ElectionRepository electionRepository;
    @Autowired
    private UserRepository userRepository;

    public Candidate addCandidate(Long electionId, Candidate candidate) {
        Election election = electionRepository.findById(electionId).orElseThrow(() -> new RuntimeException("Election not found"));
        candidate.setElection(election);
        // User ID is optional for candidate as per schema (user_id nullable)
        // If provided, we link it
        if (candidate.getUser() != null && candidate.getUser().getUserId() != null) {
            User user = userRepository.findById(candidate.getUser().getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            candidate.setUser(user);
        }
        return candidateRepository.save(candidate);
    }

    public List<Candidate> getCandidatesByElection(Long electionId) {
        return candidateRepository.findByElection_ElectionId(electionId);
    }
}
