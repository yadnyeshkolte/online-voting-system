package com.project.onlinevotingsystem.candidate.controller;

import com.project.onlinevotingsystem.candidate.model.Candidate;
import com.project.onlinevotingsystem.candidate.service.CandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @GetMapping("/election/{electionId}")
    public List<Candidate> getCandidates(@PathVariable Long electionId) {
        return candidateService.getCandidatesByElection(electionId);
    }

    // Admin Only - typically
    @PostMapping("/election/{electionId}")
    public ResponseEntity<?> addCandidate(@PathVariable Long electionId, @RequestBody Candidate candidate) {
        try {
            return ResponseEntity.ok(candidateService.addCandidate(electionId, candidate));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
