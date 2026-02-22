package com.project.onlinevotingsystem.controller;

import com.project.onlinevotingsystem.dto.UserUpdateDTO;
import com.project.onlinevotingsystem.entity.User;
import com.project.onlinevotingsystem.service.ProfileImageStorageService;
import com.project.onlinevotingsystem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProfileImageStorageService profileImageStorageService;

    private static final String DEFAULT_PROFILE_SVG = "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 160 160'>"
            + "<rect width='160' height='160' fill='#f1f3f5'/>"
            + "<circle cx='80' cy='58' r='30' fill='#adb5bd'/>"
            + "<path d='M30 140c0-28 22-46 50-46s50 18 50 46' fill='#adb5bd'/>"
            + "</svg>";

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
            String storedReference = profileImageStorageService.storeUserProfileImage(userId, file);
            userService.updateUserProfilePhoto(userId, storedReference);
            return ResponseEntity.ok("Photo uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to upload photo: " + e.getMessage());
        }
    }

    @GetMapping("/profile/photo/{filename:.+}")
    public ResponseEntity<Resource> serveProfilePhoto(@PathVariable String filename) {
        try {
            Path requestedFile = profileImageStorageService.resolveLocalImagePath(filename);
            if (Files.exists(requestedFile) && Files.isReadable(requestedFile)) {
                return serveLocalFile(requestedFile);
            }

            Optional<Path> defaultFile = profileImageStorageService.findDefaultLocalImagePath();
            if (defaultFile.isPresent()) {
                return serveLocalFile(defaultFile.get());
            }
        } catch (Exception e) {
            // Return inline fallback image
        }

        ByteArrayResource fallback = new ByteArrayResource(DEFAULT_PROFILE_SVG.getBytes(StandardCharsets.UTF_8));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "image/svg+xml")
                .body(fallback);
    }

    private ResponseEntity<Resource> serveLocalFile(Path filePath) throws Exception {
        Resource resource = new UrlResource(filePath.toUri());
        String contentType = Files.probeContentType(filePath);
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(resource);
    }
}
