package com.project.onlinevotingsystem.controller;

import com.project.onlinevotingsystem.dto.UserUpdateDTO;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Assuming the principal is the User object or details containing the ID
        // Adjust based on your CustomUserDetailsService implementation
        // If CustomUserDetails implements UserDetails and has getUser() method:
        if (auth != null && auth.getPrincipal() instanceof com.project.onlinevotingsystem.service.CustomUserDetailsService.CustomUserDetails) {
             return ((com.project.onlinevotingsystem.service.CustomUserDetailsService.CustomUserDetails) auth.getPrincipal()).getUser().getUserId();
        }
        throw new RuntimeException("User not authenticated");
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile() {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(@RequestBody UserUpdateDTO dto) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(userService.updateUserProfile(userId, dto));
    }

    @PostMapping("/profile/photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        Long userId = getCurrentUserId();
        try {
            // Sanitize and create filename
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = StringUtils.getFilenameExtension(originalFileName);
            String fileName = userId + "." + extension;
            
            Path uploadPath = Paths.get("user_uploads/profiles/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            userService.updateUserProfilePhoto(userId, fileName);

            return ResponseEntity.ok("Photo uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload photo: " + e.getMessage());
        }
    }

    @GetMapping("/profile/photo/{filename:.+}")
    public ResponseEntity<Resource> serveProfilePhoto(@PathVariable String filename) {
        try {
            Path file = Paths.get("user_uploads/profiles/").resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file))
                        .body(resource);
            } else {
                Path defaultFile = Paths.get("user_uploads/profiles/").resolve("default.png");
                Resource defaultResource = new UrlResource(defaultFile.toUri());
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(defaultFile))
                        .body(defaultResource);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
