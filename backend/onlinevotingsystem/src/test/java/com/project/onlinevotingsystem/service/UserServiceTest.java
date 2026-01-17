package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.dto.UserUpdateDTO;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.repository.CandidateRepository;
import com.project.onlinevotingsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserProfile_Success() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserProfile(1L);
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
    }

    @Test
    void getUserProfile_NotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.getUserProfile(1L));
    }

    @Test
    void updateUserProfile_Success() {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("old@test.com");
        
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("new@test.com");
        dto.setCity("New City");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.updateUserProfile(1L, dto);

        assertEquals("new@test.com", result.getEmail());
        assertEquals("New City", result.getCity());
    }

    @Test
    void updateUserProfilePhoto_Success() {
        User user = new User();
        user.setUserId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUserProfilePhoto(1L, "photo.jpg");

        assertEquals("/api/user/profile/photo/photo.jpg", user.getProfileImageUrl());
        verify(userRepository).save(user);
    }
}
