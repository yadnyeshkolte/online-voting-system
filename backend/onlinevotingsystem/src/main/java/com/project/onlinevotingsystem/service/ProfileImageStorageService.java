package com.project.onlinevotingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class ProfileImageStorageService {

    private static final String STORAGE_MODE_CLOUDINARY = "cloudinary";
    private static final java.util.List<String> DEFAULT_IMAGE_NAMES = Arrays.asList("default.png", "default.jpg", "default.jpeg");

    private final RestTemplate restTemplate;

    @Value("${app.profile-image.storage:local}")
    private String storageMode;

    @Value("${app.file-storage.path:user_uploads/profiles/}")
    private String fileStoragePath;

    @Value("${app.cloudinary.cloud-name:}")
    private String cloudinaryCloudName;

    @Value("${app.cloudinary.api-key:}")
    private String cloudinaryApiKey;

    @Value("${app.cloudinary.api-secret:}")
    private String cloudinaryApiSecret;

    @Value("${app.cloudinary.folder:online-voting/profiles}")
    private String cloudinaryFolder;

    public String storeUserProfileImage(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Profile image file is required.");
        }

        String mode = storageMode == null ? "local" : storageMode.trim().toLowerCase(Locale.ROOT);
        if (STORAGE_MODE_CLOUDINARY.equals(mode)) {
            return uploadToCloudinary(userId, file);
        }

        return storeLocally(userId, file);
    }

    public Path resolveLocalImagePath(String imageReference) {
        String fileName = extractFileName(imageReference);
        if (!StringUtils.hasText(fileName)) {
            throw new RuntimeException("Invalid profile image reference.");
        }

        Path basePath = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        Path resolvedPath = basePath.resolve(fileName).normalize();
        if (!resolvedPath.startsWith(basePath)) {
            throw new RuntimeException("Invalid profile image path.");
        }
        return resolvedPath;
    }

    public Optional<Path> findDefaultLocalImagePath() {
        Path basePath = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        for (String defaultImageName : DEFAULT_IMAGE_NAMES) {
            Path candidate = basePath.resolve(defaultImageName).normalize();
            if (Files.exists(candidate) && Files.isReadable(candidate)) {
                return Optional.of(candidate);
            }
        }
        return Optional.empty();
    }

    public boolean isDefaultProfileImage(String profileImageReference) {
        String fileName = extractFileName(profileImageReference);
        if (!StringUtils.hasText(fileName)) {
            return true;
        }

        String lowerName = fileName.toLowerCase(Locale.ROOT);
        return DEFAULT_IMAGE_NAMES.contains(lowerName) || lowerName.startsWith("default.");
    }

    public boolean isRemoteUrl(String profileImageReference) {
        if (!StringUtils.hasText(profileImageReference)) {
            return false;
        }
        String lower = profileImageReference.trim().toLowerCase(Locale.ROOT);
        return lower.startsWith("http://") || lower.startsWith("https://");
    }

    public ResolvedImageForVerification resolveForVerification(String profileImageReference) {
        if (!StringUtils.hasText(profileImageReference)) {
            throw new RuntimeException("User does not have a profile image for verification.");
        }

        if (isRemoteUrl(profileImageReference)) {
            return downloadRemoteImage(profileImageReference.trim());
        }

        Path localPath = resolveLocalImagePath(profileImageReference);
        if (!Files.exists(localPath) || !Files.isReadable(localPath)) {
            throw new RuntimeException("Stored profile image not found on server.");
        }
        return new ResolvedImageForVerification(localPath.toFile(), false);
    }

    public void cleanupResolvedVerificationImage(ResolvedImageForVerification image) {
        if (image == null || !image.temporary() || image.file() == null) {
            return;
        }
        try {
            Files.deleteIfExists(image.file().toPath());
        } catch (IOException ignored) {
            // No-op cleanup failure
        }
    }

    public String extractFileName(String imageReference) {
        if (!StringUtils.hasText(imageReference)) {
            return "";
        }

        String cleaned = imageReference.trim();
        int queryIndex = cleaned.indexOf('?');
        if (queryIndex >= 0) {
            cleaned = cleaned.substring(0, queryIndex);
        }

        int fragmentIndex = cleaned.indexOf('#');
        if (fragmentIndex >= 0) {
            cleaned = cleaned.substring(0, fragmentIndex);
        }

        cleaned = cleaned.replace("\\", "/");
        int lastSlashIndex = cleaned.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            cleaned = cleaned.substring(lastSlashIndex + 1);
        }

        return StringUtils.cleanPath(cleaned);
    }

    private String storeLocally(Long userId, MultipartFile file) {
        try {
            Path basePath = Paths.get(fileStoragePath).toAbsolutePath().normalize();
            Files.createDirectories(basePath);

            String extension = resolveImageExtension(file);
            String fileName = userId + "." + extension;
            Path targetPath = basePath.resolve(fileName).normalize();
            if (!targetPath.startsWith(basePath)) {
                throw new RuntimeException("Invalid target path for profile image.");
            }

            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return "/api/user/profile/photo/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store profile image locally.", e);
        }
    }

    private String uploadToCloudinary(Long userId, MultipartFile file) {
        if (!StringUtils.hasText(cloudinaryCloudName)
                || !StringUtils.hasText(cloudinaryApiKey)
                || !StringUtils.hasText(cloudinaryApiSecret)) {
            throw new RuntimeException("Cloudinary is enabled but credentials are missing.");
        }

        try {
            long timestamp = Instant.now().getEpochSecond();
            String publicId = "user-" + userId;

            Map<String, String> signatureParams = new TreeMap<>();
            signatureParams.put("folder", cloudinaryFolder);
            signatureParams.put("invalidate", "true");
            signatureParams.put("overwrite", "true");
            signatureParams.put("public_id", publicId);
            signatureParams.put("timestamp", String.valueOf(timestamp));

            String signature = buildCloudinarySignature(signatureParams);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", asByteArrayResource(file));
            body.add("api_key", cloudinaryApiKey);
            body.add("timestamp", String.valueOf(timestamp));
            body.add("folder", cloudinaryFolder);
            body.add("public_id", publicId);
            body.add("overwrite", "true");
            body.add("invalidate", "true");
            body.add("signature", signature);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            String uploadUrl = "https://api.cloudinary.com/v1_1/" + cloudinaryCloudName + "/image/upload";
            ResponseEntity<Map> response = restTemplate.postForEntity(uploadUrl, request, Map.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Cloudinary upload failed.");
            }

            Object secureUrl = response.getBody().get("secure_url");
            if (secureUrl == null || !StringUtils.hasText(secureUrl.toString())) {
                throw new RuntimeException("Cloudinary upload did not return secure_url.");
            }

            return secureUrl.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not read profile image file.", e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload profile image to Cloudinary.", e);
        }
    }

    private String resolveImageExtension(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFileName);
        if (StringUtils.hasText(extension)) {
            String normalized = extension.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
            if (StringUtils.hasText(normalized)) {
                return normalized;
            }
        }

        String contentType = file.getContentType();
        if (contentType != null) {
            String lowerContentType = contentType.toLowerCase(Locale.ROOT);
            if (lowerContentType.contains("png")) {
                return "png";
            }
            if (lowerContentType.contains("webp")) {
                return "webp";
            }
        }
        return "jpg";
    }

    private ByteArrayResource asByteArrayResource(MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String originalFileName = file.getOriginalFilename();
        final String safeName = StringUtils.hasText(originalFileName)
                ? StringUtils.cleanPath(originalFileName)
                : "profile.jpg";

        return new ByteArrayResource(bytes) {
            @Override
            public String getFilename() {
                return safeName;
            }
        };
    }

    private String buildCloudinarySignature(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!StringUtils.hasText(entry.getValue())) {
                continue;
            }
            if (!first) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(entry.getValue());
            first = false;
        }
        return sha1Hex(builder + cloudinaryApiSecret);
    }

    private String sha1Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Cloudinary signature.", e);
        }
    }

    private ResolvedImageForVerification downloadRemoteImage(String remoteUrl) {
        try {
            ResponseEntity<byte[]> response = restTemplate.getForEntity(remoteUrl, byte[].class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().length == 0) {
                throw new RuntimeException("Could not download stored profile image for verification.");
            }

            String suffix = ".jpg";
            MediaType contentType = response.getHeaders().getContentType();
            if (contentType != null && MediaType.IMAGE_PNG.isCompatibleWith(contentType)) {
                suffix = ".png";
            } else if (contentType != null && "image".equalsIgnoreCase(contentType.getType()) && StringUtils.hasText(contentType.getSubtype())) {
                suffix = "." + contentType.getSubtype().toLowerCase(Locale.ROOT);
            }

            File tempFile = File.createTempFile("stored-profile-", suffix);
            Files.write(tempFile.toPath(), response.getBody());
            return new ResolvedImageForVerification(tempFile, true);
        } catch (IOException e) {
            throw new RuntimeException("Could not prepare stored profile image for verification.", e);
        }
    }

    public record ResolvedImageForVerification(File file, boolean temporary) {}
}
