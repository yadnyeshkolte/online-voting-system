package com.project.onlinevotingsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.onlinevotingsystem.dto.UserUpdateDTO;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.service.CustomUserDetailsService;
import com.project.onlinevotingsystem.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters to simplify context setup
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // These might be required if SecurityConfig is picked up even with addFilters=false
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private com.project.onlinevotingsystem.config.JwtUtils jwtUtils;
    @MockBean
    private com.project.onlinevotingsystem.config.JwtRequestFilter jwtRequestFilter;
    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    @MockBean
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Mock Security Context for getCurrentUserId()
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        
        CustomUserDetailsService.CustomUserDetails principal = mock(CustomUserDetailsService.CustomUserDetails.class);
        User user = new User();
        user.setUserId(1L);
        when(principal.getUser()).thenReturn(user);

        when(authentication.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getProfile_Success() throws Exception {
        User user = new User();
        user.setUserId(1L);
        user.setEmail("test@test.com");
        
        when(userService.getUserProfile(1L)).thenReturn(user);

        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@test.com"));
    }

    @Test
    void updateProfile_Success() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setEmail("updated@test.com");

        User updatedUser = new User();
        updatedUser.setUserId(1L);
        updatedUser.setEmail("updated@test.com");

        when(userService.updateUserProfile(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/user/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@test.com"));
    }
}
