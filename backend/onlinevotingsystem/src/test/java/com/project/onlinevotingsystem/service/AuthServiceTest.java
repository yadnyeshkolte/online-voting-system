package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.config.JwtUtils;
import com.project.onlinevotingsystem.dto.AuthResponse;
import com.project.onlinevotingsystem.dto.LoginRequest;
import com.project.onlinevotingsystem.dto.RegisterRequest;
import com.project.onlinevotingsystem.entity.Admin;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.repository.AdminRepository;
import com.project.onlinevotingsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationService verificationService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_Success_User() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");

        UserDetails userDetails = mock(UserDetails.class);
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");

        when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
        when(adminRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(userDetails, "USER", 1L)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getJwt());
        assertEquals("USER", response.getRole());
        assertEquals(1L, response.getUserId());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void login_Success_Admin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.com");
        request.setPassword("adminpass");

        UserDetails userDetails = mock(UserDetails.class);
        Admin admin = new Admin();
        admin.setAdminId(10L);
        admin.setEmail("admin@example.com");

        when(userDetailsService.loadUserByUsername("admin@example.com")).thenReturn(userDetails);
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(admin));
        when(jwtUtils.generateToken(userDetails, "ADMIN", 10L)).thenReturn("admin-token");

        AuthResponse response = authService.login(request);

        assertEquals("admin-token", response.getJwt());
        assertEquals("ADMIN", response.getRole());
        assertEquals(10L, response.getUserId());
    }

    @Test
    void register_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("pass");
        request.setFullName("John Doe");
        request.setAadharNumber("1234");
        request.setVoterIdNumber("VOTER123");
        request.setDateOfBirth(LocalDate.of(1990, 1, 1));

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(userRepository.existsByAadharNumber(request.getAadharNumber())).thenReturn(false);
        when(userRepository.existsByVoterIdNumber(request.getVoterIdNumber())).thenReturn(false);
        when(verificationService.verifyUserIdentity(any(), any(), any(), any())).thenReturn(true);
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(5L);
            return u;
        });

        User registeredUser = authService.register(request);

        assertNotNull(registeredUser);
        assertEquals("newuser@example.com", registeredUser.getEmail());
        assertEquals("encodedPass", registeredUser.getPasswordHash());
        assertTrue(registeredUser.getIsVerified());
    }

    @Test
    void register_Fail_EmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_Fail_VerificationFailed() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("fake@example.com");
        request.setAadharNumber("111");
        request.setVoterIdNumber("222");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.existsByAadharNumber(any())).thenReturn(false);
        when(userRepository.existsByVoterIdNumber(any())).thenReturn(false);
        when(verificationService.verifyUserIdentity(any(), any(), any(), any())).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> authService.register(request));
        assertTrue(exception.getMessage().contains("Identity verification failed"));
    }
}
