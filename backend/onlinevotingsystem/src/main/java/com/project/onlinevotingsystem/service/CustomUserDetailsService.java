package com.project.onlinevotingsystem.service;

import com.project.onlinevotingsystem.entity.Admin;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.repository.AdminRepository;
import com.project.onlinevotingsystem.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    public CustomUserDetailsService(UserRepository userRepository, AdminRepository adminRepository) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
    }

    public static class CustomUserDetails implements UserDetails {
        private final User user;

        public CustomUserDetails(User user) {
            this.user = user;
        }

        public User getUser() {
            return user;
        }

        @Override
        public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }

        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() { return true; }
        @Override
        public boolean isAccountNonLocked() { return true; }
        @Override
        public boolean isCredentialsNonExpired() { return true; }
        @Override
        public boolean isEnabled() { return user.getIsActive(); }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Try finding in Admin first
        Optional<Admin> admin = adminRepository.findByEmail(username);
        if (admin.isPresent()) {
            return new org.springframework.security.core.userdetails.User(
                    admin.get().getEmail(),
                    admin.get().getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // Try finding in User
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            return new CustomUserDetails(user.get());
        }

        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
