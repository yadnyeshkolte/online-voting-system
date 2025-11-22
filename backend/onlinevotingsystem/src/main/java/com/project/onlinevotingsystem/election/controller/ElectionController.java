package com.project.onlinevotingsystem.election.controller;

import com.project.onlinevotingsystem.election.model.Election;
import com.project.onlinevotingsystem.election.model.ElectionStatus;
import com.project.onlinevotingsystem.election.service.ElectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/elections")
public class ElectionController {

    @Autowired
    private ElectionService electionService;

    // Public or Voter accessible to see active elections
    @GetMapping("/active")
    public List<Election> getActiveElections() {
        return electionService.getActiveElections();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Election> getElection(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getElection(id));
    }

    // Admin only
    @GetMapping("/all")
    public List<Election> getAllElections() {
        return electionService.getAllElections();
    }

    @PostMapping
    public ResponseEntity<?> createElection(@RequestBody Election election, Authentication authentication) {
        try {
            return ResponseEntity.ok(electionService.createElection(election, authentication.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam ElectionStatus status) {
        try {
            return ResponseEntity.ok(electionService.updateStatus(id, status));
        } catch (RuntimeException e) {
             return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
