package com.project.onlinevotingsystem.controller;

import com.project.onlinevotingsystem.dto.AdminUserUpdateDTO;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> searchUsers(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(userService.searchUsers(query));
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody AdminUserUpdateDTO dto) {
        return ResponseEntity.ok(userService.adminUpdateUser(userId, dto));
    }

    @PutMapping("/candidates/{candidateId}/image")
    public ResponseEntity<Void> updateCandidateImage(@PathVariable Long candidateId, @RequestParam("image") MultipartFile image) throws IOException {
        userService.updateCandidateImage(candidateId, image.getBytes());
        return ResponseEntity.ok().build();
    }
}
