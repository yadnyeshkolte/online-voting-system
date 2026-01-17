package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.entity.*;
import com.project.onlinevotingsystem.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoteServiceTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private VoterElectionStatusRepository voterElectionStatusRepository;
    @Mock
    private ElectionRepository electionRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private ElectionService electionService;

    @InjectMocks
    private VoteService voteService;

    @Test
    void castVote_Success() {
        Long electionId = 1L;
        Long userId = 2L;
        Long candidateId = 3L;

        Election election = new Election();
        election.setElectionId(electionId);
        election.setStatus(ElectionStatus.ACTIVE);

        when(electionRepository.findById(electionId)).thenReturn(Optional.of(election));
        when(voterElectionStatusRepository.findByElection_ElectionIdAndUser_UserId(electionId, userId))
                .thenReturn(Optional.empty());
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(candidateRepository.findById(candidateId)).thenReturn(Optional.of(new Candidate()));
        when(voteRepository.save(any(Vote.class))).thenAnswer(i -> i.getArgument(0));

        Vote vote = voteService.castVote(electionId, userId, candidateId);

        assertNotNull(vote);
        assertNotNull(vote.getVoteHash());
        verify(voterElectionStatusRepository).save(any(VoterElectionStatus.class));
        verify(electionService).calculateResults(electionId);
    }

    @Test
    void castVote_Fail_ElectionNotActive() {
        Election election = new Election();
        election.setStatus(ElectionStatus.COMPLETED);
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));

        assertThrows(RuntimeException.class, () -> voteService.castVote(1L, 2L, 3L));
    }

    @Test
    void castVote_Fail_AlreadyVoted() {
        Election election = new Election();
        election.setStatus(ElectionStatus.ACTIVE);
        when(electionRepository.findById(1L)).thenReturn(Optional.of(election));

        VoterElectionStatus status = new VoterElectionStatus();
        status.setHasVoted(true);
        when(voterElectionStatusRepository.findByElection_ElectionIdAndUser_UserId(1L, 2L))
                .thenReturn(Optional.of(status));

        assertThrows(RuntimeException.class, () -> voteService.castVote(1L, 2L, 3L));
    }
}
