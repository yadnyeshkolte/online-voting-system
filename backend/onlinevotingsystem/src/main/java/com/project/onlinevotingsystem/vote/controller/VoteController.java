package com.project.onlinevotingsystem.vote.controller;

import com.project.onlinevotingsystem.vote.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/cast")
    public ResponseEntity<?> castVote(@RequestBody Map<String, Long> payload, Authentication authentication) {
        Long electionId = payload.get("electionId");
        Long candidateId = payload.get("candidateId");

        if (electionId == null || candidateId == null) {
            return ResponseEntity.badRequest().body("Missing electionId or candidateId");
        }

        try {
            return ResponseEntity.ok(voteService.castVote(authentication.getName(), electionId, candidateId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/status/{electionId}")
    public ResponseEntity<?> getVoteStatus(@PathVariable Long electionId, Authentication authentication) {
         try {
            boolean hasVoted = voteService.hasVoted(authentication.getName(), electionId);
            return ResponseEntity.ok(Map.of("hasVoted", hasVoted));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
