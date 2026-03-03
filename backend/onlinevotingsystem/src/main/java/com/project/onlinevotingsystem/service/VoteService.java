package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.entity.*;
import com.project.onlinevotingsystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.Map;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final VoterElectionStatusRepository voterElectionStatusRepository;
    private final ElectionRepository electionRepository;
    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final ElectionService electionService;
    private final RestTemplate restTemplate;
    private final ProfileImageStorageService profileImageStorageService;

    @Value("${app.image-verify-service.url}")
    private String imageVerifyServiceUrl;

    @Transactional
    public Vote castVote(Long electionId, Long userId, Long candidateId, MultipartFile capturedImage) {
        // Check if election is active
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new RuntimeException("Election not found"));

        if (election.getStatus() != ElectionStatus.ACTIVE) {
            throw new RuntimeException("Election is not active");
        }

        // Check if user has already voted
        Optional<VoterElectionStatus> existingStatusOpt = voterElectionStatusRepository.findByElection_ElectionIdAndUser_UserId(electionId, userId);
        
        if (existingStatusOpt.isPresent() && Boolean.TRUE.equals(existingStatusOpt.get().getHasVoted())) {
            throw new RuntimeException("User has already voted in this election");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify Face
        if (capturedImage != null && !capturedImage.isEmpty()) {
            verifyFace(user, capturedImage);
        } else {
             // Depending on requirements, we might enforce face verification
             throw new RuntimeException("Face verification is required. Please provide a captured image.");
        }

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        // Create Vote
        Vote vote = new Vote();
        vote.setElection(election);
        vote.setUser(user);
        vote.setCandidate(candidate);
        vote.setVoteHash(UUID.randomUUID().toString());
        
        try {
            vote = voteRepository.save(vote);

            // Update Status
            VoterElectionStatus status;
            if (existingStatusOpt.isPresent()) {
                status = existingStatusOpt.get();
            } else {
                status = new VoterElectionStatus();
                status.setElection(election);
                status.setUser(user);
            }
            status.setHasVoted(true);
            status.setVotedAt(LocalDateTime.now());
            voterElectionStatusRepository.save(status);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("You have already voted in this election.");
        }

        // Update Election Results
        electionService.calculateResults(electionId);

        return vote;
    }

    private void verifyFace(User user, MultipartFile capturedImage) {
        String profileImageReference = user.getProfileImageUrl();

        if (!StringUtils.hasText(profileImageReference)) {
            throw new RuntimeException("User does not have a profile image for verification.");
        }

        if (profileImageStorageService.isDefaultProfileImage(profileImageReference)) {
            throw new RuntimeException("Please upload your own profile photo before voting.");
        }

        ProfileImageStorageService.ResolvedImageForVerification storedImage = null;
        File tempCapturedFile = null;
        try {
            storedImage = profileImageStorageService.resolveForVerification(profileImageReference);

            // Convert MultipartFile to File for RestTemplate
            String capturedFileName = capturedImage.getOriginalFilename();
            String suffix = ".jpg";
            if (StringUtils.hasText(capturedFileName) && capturedFileName.lastIndexOf('.') >= 0) {
                String extractedSuffix = capturedFileName.substring(capturedFileName.lastIndexOf('.'));
                if (extractedSuffix.length() <= 10) {
                    suffix = extractedSuffix;
                }
            }

            tempCapturedFile = File.createTempFile("captured-", suffix);
            try (FileOutputStream fos = new FileOutputStream(tempCapturedFile)) {
                fos.write(capturedImage.getBytes());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set(HttpHeaders.CONNECTION, "close");

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("storedImage", new FileSystemResource(storedImage.file()));
            body.add("capturedImage", new FileSystemResource(tempCapturedFile));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = callImageVerifyWithRetry(requestEntity);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean isMatch = (Boolean) response.getBody().get("match");
                if (!Boolean.TRUE.equals(isMatch)) {
                    throw new RuntimeException("Face verification failed. Face does not match profile.");
                }
            } else {
                 throw new RuntimeException("Face verification service error.");
            }

        } catch (HttpClientErrorException e) {
            try {
                String responseBody = e.getResponseBodyAsString();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseBody);
                if (root.has("error")) {
                    throw new RuntimeException("Face verification failed: " + root.get("error").asText());
                }
            } catch (Exception parseException) {
                // Fallback to default message
            }
            throw new RuntimeException("Face verification failed: " + e.getStatusText());
        } catch (IOException e) {
            throw new RuntimeException("Error processing images for verification.", e);
        } catch (Exception e) {
             throw new RuntimeException("Face verification failed: " + e.getMessage());
        } finally {
            if (tempCapturedFile != null && tempCapturedFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                tempCapturedFile.delete();
            }
            profileImageStorageService.cleanupResolvedVerificationImage(storedImage);
        }
    }

    private ResponseEntity<Map> callImageVerifyWithRetry(HttpEntity<MultiValueMap<String, Object>> requestEntity) {
        int maxAttempts = 2;
        ResourceAccessException lastException = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                return restTemplate.postForEntity(imageVerifyServiceUrl, requestEntity, Map.class);
            } catch (ResourceAccessException e) {
                String message = e.getMessage() == null ? "" : e.getMessage();
                boolean transientEof = message.contains("Unexpected end of file from server");
                if (!transientEof || attempt == maxAttempts) {
                    throw e;
                }
                lastException = e;
                try {
                    Thread.sleep(250);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }

        throw lastException != null ? lastException : new ResourceAccessException("Image verification service call failed.");
    }

    public boolean hasUserVoted(Long electionId, Long userId) {
        return voterElectionStatusRepository.findByElection_ElectionIdAndUser_UserId(electionId, userId)
                .map(status -> Boolean.TRUE.equals(status.getHasVoted()))
                .orElse(false);
    }
}
