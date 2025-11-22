package com.project.onlinevotingsystem.admin.controller;

import com.project.onlinevotingsystem.admin.service.AdminService;
import com.project.onlinevotingsystem.user.model.User;
import com.project.onlinevotingsystem.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/make-admin/{userId}")
    public ResponseEntity<?> makeAdmin(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(adminService.makeAdmin(userId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Admin CRUD on users if needed
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }
}
