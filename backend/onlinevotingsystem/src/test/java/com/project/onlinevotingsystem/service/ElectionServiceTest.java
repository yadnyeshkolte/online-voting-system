package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.dto.CandidateDto;
import com.project.onlinevotingsystem.dto.ElectionCreationRequest;
import com.project.onlinevotingsystem.entity.*;
import com.project.onlinevotingsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ElectionServiceTest {

    @Mock
    private ElectionRepository electionRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private ElectionResultRepository electionResultRepository;
    @Mock
    private ElectionReportRepository electionReportRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private ElectionService electionService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createElection_Success() {
        // Mock Security Context
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("admin@test.com");
        SecurityContextHolder.setContext(securityContext);

        ElectionCreationRequest request = new ElectionCreationRequest();
        request.setElectionName("Test Election");
        request.setElectionType(ElectionType.GENERAL);
        request.setStartDate(LocalDateTime.now().plusDays(1));
        request.setEndDate(LocalDateTime.now().plusDays(2));
        
        List<CandidateDto> candidates = new ArrayList<>();
        CandidateDto c1 = new CandidateDto();
        c1.setUserId(1L);
        c1.setPartyName("Party A");
        candidates.add(c1);
        request.setCandidates(candidates);

        Admin admin = new Admin();
        admin.setAdminId(1L);
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(electionRepository.save(any(Election.class))).thenAnswer(i -> {
            Election e = i.getArgument(0);
            e.setElectionId(100L);
            return e;
        });
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(new Candidate());

        Election result = electionService.createElection(request);

        assertNotNull(result);
        assertEquals("Test Election", result.getElectionName());
        assertEquals(ElectionStatus.DRAFT, result.getStatus());
        assertEquals(1L, result.getCreatedBy());
        verify(candidateRepository, times(1)).save(any(Candidate.class));
    }

    @Test
    void calculateResults_Success() {
        Long electionId = 1L;
        Election election = new Election();
        election.setElectionId(electionId);
        
        Candidate c1 = new Candidate(); c1.setCandidateId(10L);
        Candidate c2 = new Candidate(); c2.setCandidateId(11L);
        List<Candidate> candidates = List.of(c1, c2);

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));
        when(candidateRepository.findByElection_ElectionIdOrderByCandidateIdAsc(electionId)).thenReturn(candidates);
        when(voteRepository.countByElection_ElectionId(electionId)).thenReturn(100L);
        
        when(voteRepository.countByCandidate_CandidateId(10L)).thenReturn(60L);
        when(voteRepository.countByCandidate_CandidateId(11L)).thenReturn(40L);

        when(electionResultRepository.findByElection_ElectionIdAndCandidate_CandidateId(eq(electionId), anyLong()))
                .thenReturn(Optional.empty());

        // Mock report generation calls
        when(electionResultRepository.findByElection_ElectionIdOrderByRankPositionAsc(electionId)).thenReturn(new ArrayList<>());
        when(userRepository.count()).thenReturn(200L);

        electionService.calculateResults(electionId);

        verify(electionResultRepository, atLeast(2)).save(any(ElectionResult.class));
        verify(electionReportRepository).save(any(ElectionReport.class));
    }
}
