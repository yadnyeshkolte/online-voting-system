package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.dto.AdminUserUpdateDTO;
import com.project.onlinevotingsystem.dto.UserUpdateDTO;
import com.project.onlinevotingsystem.entity.Candidate;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.repository.CandidateRepository;
import com.project.onlinevotingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    @Transactional
    public User updateUserProfile(Long userId, UserUpdateDTO dto) {
        User user = getUserProfile(userId);

        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getPincode() != null) user.setPincode(dto.getPincode());
        if (dto.getProfileImageUrl() != null) user.setProfileImageUrl(dto.getProfileImageUrl());

        // Can add PAN/Passport if not present
        if (user.getPanCardNumber() == null && dto.getPanCardNumber() != null) {
            user.setPanCardNumber(dto.getPanCardNumber());
        }
        if (user.getPassportNumber() == null && dto.getPassportNumber() != null) {
            user.setPassportNumber(dto.getPassportNumber());
        }

        return userRepository.save(user);
    }

    @Transactional
    public User adminUpdateUser(Long userId, AdminUserUpdateDTO dto) {
        User user = getUserProfile(userId);

        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getDateOfBirth() != null) user.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getGender() != null) user.setGender(dto.getGender());
        if (dto.getAddress() != null) user.setAddress(dto.getAddress());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getPincode() != null) user.setPincode(dto.getPincode());
        if (dto.getProfileImageUrl() != null) user.setProfileImageUrl(dto.getProfileImageUrl());

        // Admin can add PAN/Passport if missing
        if (user.getPanCardNumber() == null && dto.getPanCardNumber() != null) {
            user.setPanCardNumber(dto.getPanCardNumber());
        }
        if (user.getPassportNumber() == null && dto.getPassportNumber() != null) {
            user.setPassportNumber(dto.getPassportNumber());
        }

        // Admin cannot edit Aadhar/Voter ID (implied by not including them in DTO mapping or logic)

        return userRepository.save(user);
    }

    @Transactional
    public void updateCandidateImage(Long candidateId, byte[] image) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
        candidate.setCandidatePhoto(image);
        candidateRepository.save(candidate);
    }

    @Transactional
    public void updateUserProfilePhoto(Long userId, String profileImageReference) {
        User user = getUserProfile(userId);
        user.setProfileImageUrl(profileImageReference);
        userRepository.save(user);
    }
}
