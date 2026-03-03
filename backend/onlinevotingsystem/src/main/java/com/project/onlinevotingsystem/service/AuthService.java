package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.config.JwtUtils;
import com.project.onlinevotingsystem.dto.AuthResponse;
import com.project.onlinevotingsystem.dto.LoginRequest;
import com.project.onlinevotingsystem.dto.RegisterRequest;
import com.project.onlinevotingsystem.entity.Admin;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.repository.AdminRepository;
import com.project.onlinevotingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationService verificationService;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentifier());

        String role = "USER";
        Long id = null;

        Optional<Admin> admin = adminRepository.findByEmail(request.getIdentifier());
        if (admin.isPresent()) {
            role = "ADMIN";
            id = admin.get().getAdminId();
        } else {
            // Try Voter ID first
            Optional<User> user = userRepository.findByVoterIdNumber(request.getIdentifier());
            if (user.isPresent()) {
                role = "USER";
                id = user.get().getUserId();
            } else {
                // Fallback to Email (since CustomUserDetailsService allows it)
                Optional<User> userByEmail = userRepository.findByEmail(request.getIdentifier());
                if (userByEmail.isPresent()) {
                    role = "USER";
                    id = userByEmail.get().getUserId();
                }
            }
        }

        final String jwt = jwtUtils.generateToken(userDetails, role, id);
        return new AuthResponse(jwt, role, id);
    }

    public User register(RegisterRequest request) {
        // Age validation
        if (request.getDateOfBirth() != null) {
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.Period period = java.time.Period.between(request.getDateOfBirth(), today);
            if (period.getYears() < 18) {
                throw new RuntimeException("You must be at least 18 years old to register.");
            }
        } else {
             throw new RuntimeException("Date of birth is required.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }
        if (userRepository.existsByAadharNumber(request.getAadharNumber())) {
            throw new RuntimeException("Aadhar number already registered");
        }
        if (userRepository.existsByVoterIdNumber(request.getVoterIdNumber())) {
            throw new RuntimeException("Voter ID number already registered");
        }

        // Check verification (Both required)
        boolean isVerified = verificationService.verifyUserIdentity(
                request.getAadharNumber(),
                request.getVoterIdNumber(),
                request.getFullName(),
                request.getDateOfBirth()
        );

        if (!isVerified) {
             throw new RuntimeException("Identity verification failed. Please check your Aadhar and Voter ID details.");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity());
        user.setState(request.getState());
        user.setPincode(request.getPincode());
        user.setAadharNumber(request.getAadharNumber());
        user.setVoterIdNumber(request.getVoterIdNumber());
        String profileImageUrl = request.getProfileImageUrl();
        if (!StringUtils.hasText(profileImageUrl)) {
            profileImageUrl = "/api/user/profile/photo/default.png";
        }
        user.setProfileImageUrl(profileImageUrl);
        user.setIsVerified(isVerified);

        return userRepository.save(user);
    }
}
