package com.project.onlinevotingsystem.admin.service;

import com.project.onlinevotingsystem.admin.model.Admin;
import com.project.onlinevotingsystem.admin.repository.AdminRepository;
import com.project.onlinevotingsystem.user.model.Role;
import com.project.onlinevotingsystem.user.model.User;
import com.project.onlinevotingsystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Admin makeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return adminRepository.findByUser_UserId(userId).orElseThrow();
        }

        user.setRole(Role.ADMIN);
        userRepository.save(user);

        Admin admin = new Admin();
        admin.setUser(user);
        return adminRepository.save(admin);
    }
}
