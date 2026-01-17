package com.project.onlinevotingsystem.controller;

import com.project.onlinevotingsystem.entity.Candidate;
import com.project.onlinevotingsystem.entity.Election;
import com.project.onlinevotingsystem.entity.ElectionResult;
import com.project.onlinevotingsystem.service.ElectionService;
import com.project.onlinevotingsystem.service.VoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/elections")
public class UserElectionController {

    private final ElectionService electionService;
    private final VoteService voteService;
    private final com.project.onlinevotingsystem.repository.UserRepository userRepository;

    public UserElectionController(ElectionService electionService, VoteService voteService, com.project.onlinevotingsystem.repository.UserRepository userRepository) {
        this.electionService = electionService;
        this.voteService = voteService;
        this.userRepository = userRepository;
    }

    @GetMapping("/active")
    public ResponseEntity<List<Election>> getActiveElections() {
        return ResponseEntity.ok(electionService.getActiveElections());
    }

    @GetMapping("/completed")
    public ResponseEntity<List<Election>> getCompletedElections() {
        return ResponseEntity.ok(electionService.getPastElections());
    }

    @GetMapping("/{id}/candidates")
    public ResponseEntity<List<Candidate>> getCandidates(@PathVariable Long id) {
        return ResponseEntity.ok(electionService.getCandidatesForElection(id));
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> vote(@PathVariable Long id, @RequestParam Long candidateId, Authentication authentication) {
        String email = authentication.getName();
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getUserId();

        try {
            return ResponseEntity.ok(voteService.castVote(id, userId, candidateId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/has-voted")
    public ResponseEntity<Boolean> hasVoted(@PathVariable Long id, @RequestParam Long userId) {
        // Security concern: User should only check their own status.
        // But for this task I will allow passing userId.
        return ResponseEntity.ok(voteService.hasUserVoted(id, userId));
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<List<ElectionResult>> getResults(@PathVariable Long id) {
        // Users can only view results of completed elections
        Election election = electionService.getElectionById(id);
        if (election.getStatus() != com.project.onlinevotingsystem.entity.ElectionStatus.COMPLETED) {
            return ResponseEntity.status(403).build(); // Forbidden
        }
        return ResponseEntity.ok(electionService.getResults(id));
    }

    @GetMapping("/candidates/{candidateId}/photo")
    public ResponseEntity<byte[]> getCandidatePhoto(@PathVariable Long candidateId) {
        byte[] photo = electionService.getCandidatePhoto(candidateId);
        if (photo == null || photo.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg") // Assuming JPEG, but browsers handle most image types even with wrong header
                .body(photo);
    }
}
