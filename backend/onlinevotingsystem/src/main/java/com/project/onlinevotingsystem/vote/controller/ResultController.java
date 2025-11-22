package com.project.onlinevotingsystem.vote.controller;

import com.project.onlinevotingsystem.vote.model.ElectionResult;
import com.project.onlinevotingsystem.vote.service.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    @Autowired
    private ResultService resultService;

    // Admin triggers calculation
    @PostMapping("/calculate/{electionId}")
    public ResponseEntity<List<ElectionResult>> calculateResults(@PathVariable Long electionId) {
        return ResponseEntity.ok(resultService.calculateResults(electionId));
    }

    // Public view (if published, logic should be in service but kept simple here)
    @GetMapping("/{electionId}")
    public ResponseEntity<List<ElectionResult>> getResults(@PathVariable Long electionId) {
        return ResponseEntity.ok(resultService.getResults(electionId));
    }
}
