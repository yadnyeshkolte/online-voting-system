package com.project.onlinevotingsystem.controller;

import com.project.onlinevotingsystem.dto.ElectionCreationRequest;
import com.project.onlinevotingsystem.entity.Candidate;
import com.project.onlinevotingsystem.entity.Election;
import com.project.onlinevotingsystem.entity.ElectionResult;
import com.project.onlinevotingsystem.entity.ElectionStatus;
import com.project.onlinevotingsystem.service.ElectionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin/elections")
public class AdminElectionController {

    private final ElectionService electionService;

    public AdminElectionController(ElectionService electionService) {
        this.electionService = electionService;
    }

    @GetMapping
    public ResponseEntity<List<Election>> getAllElections() {
        return ResponseEntity.ok(electionService.getAllElections());
    }

    @PostMapping
    public ResponseEntity<Election> createElection(@RequestBody ElectionCreationRequest electionRequest) {
        return ResponseEntity.ok(electionService.createElection(electionRequest));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Election> updateStatus(@PathVariable Long id, @RequestParam ElectionStatus status) {
        return ResponseEntity.ok(electionService.updateElectionStatus(id, status));
    }

    @PostMapping("/{id}/candidates")
    public ResponseEntity<?> addCandidate(@PathVariable Long id, @RequestBody Candidate candidate) {
        // Ensure candidate has election set
        Election election = electionService.getElectionById(id);
        if (election.getStatus() != ElectionStatus.DRAFT) {
            return ResponseEntity.badRequest().body("Cannot add candidates to non-DRAFT elections.");
        }
        candidate.setElection(election);
        return ResponseEntity.ok(electionService.addCandidate(candidate));
    }

    @PostMapping("/{id}/calculate-results")
    public ResponseEntity<?> calculateResults(@PathVariable Long id) {
        electionService.calculateResults(id);
        return ResponseEntity.ok("Results calculated");
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<List<ElectionResult>> getResults(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getResults(id));
    }

    @PostMapping("/{id}/candidates/{candidateId}/photo")
    public ResponseEntity<?> uploadCandidatePhoto(@PathVariable Long id, @PathVariable Long candidateId, @RequestParam("file") MultipartFile file) {
        try {
            electionService.updateCandidatePhoto(candidateId, file.getBytes());
            return ResponseEntity.ok("Photo uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to read file");
        }
    }
}
