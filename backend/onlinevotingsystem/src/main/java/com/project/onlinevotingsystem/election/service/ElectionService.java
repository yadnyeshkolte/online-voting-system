package com.project.onlinevotingsystem.election.service;

import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.election.model.ElectionStatus;
import com.project.onlinevotingsystem.election.repository.ElectionRepository;
import com.project.onlinevotingsystem.user.model.User;
import com.project.onlinevotingsystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ElectionService {

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private UserRepository userRepository;

    public Election createElection(Election election, String creatorEmail) {
        User creator = userRepository.findByEmail(creatorEmail).orElseThrow(() -> new RuntimeException("Creator not found"));
        election.setCreatedBy(creator);
        return electionRepository.save(election);
    }

    public List<Election> getAllElections() {
        return electionRepository.findAll();
    }

    public List<Election> getActiveElections() {
        // Logic to check date ranges + status ideally, but for now filtering by status or Active
        return electionRepository.findByStatus(ElectionStatus.ACTIVE);
    }

    public Election getElection(Long id) {
        return electionRepository.findById(id).orElseThrow(() -> new RuntimeException("Election not found"));
    }

    public Election updateStatus(Long electionId, ElectionStatus status) {
        Election election = getElection(electionId);
        election.setStatus(status);
        return electionRepository.save(election);
    }
}
