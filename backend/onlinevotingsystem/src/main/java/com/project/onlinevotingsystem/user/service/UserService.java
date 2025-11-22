package com.project.onlinevotingsystem.user.service;

import com.project.onlinevotingsystem.user.model.User;
import com.project.onlinevotingsystem.user.model.Role;
import com.project.onlinevotingsystem.user.model.RegistrationStatus;
import com.project.onlinevotingsystem.user.repository.UserRepository;
import com.project.onlinevotingsystem.verification.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationService verificationService;

    @Transactional
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // Verify ID
        boolean isVerified = verificationService.verifyId(user.getIdProofType(), user.getIdProofNumber());
        user.setVerified(isVerified);

        if (isVerified) {
            user.setRegistrationStatus(RegistrationStatus.APPROVED);
            user.setApprovedAt(LocalDateTime.now());
        } else {
            user.setRegistrationStatus(RegistrationStatus.REJECTED);
             // Alternatively, set to PENDING if manual approval is allowed, but for now we reject unverified
             throw new RuntimeException("ID Verification Failed");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user.setRole(Role.VOTER); // Default role
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
