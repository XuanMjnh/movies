package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private Path uploadRoot;

    @PostConstruct
    void initUploadDirectory() {
        try {
            uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadRoot);
        } catch (IOException e) {
            throw new IllegalStateException("Không thể khởi tạo thư mục upload", e);
        }
    }

    @Override
    public String store(MultipartFile file, String folderName, String currentValue) {
        if (file == null || file.isEmpty()) {
            return currentValue;
        }

        String safeFolderName = sanitizeFolderName(folderName);
        Path targetDirectory = uploadRoot.resolve(safeFolderName).normalize();
        validatePathInsideUploadRoot(targetDirectory);

        String storedFilename = buildStoredFilename(file.getOriginalFilename());
        Path targetFile = targetDirectory.resolve(storedFilename).normalize();
        validatePathInsideUploadRoot(targetFile);

        try {
            Files.createDirectories(targetDirectory);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetFile, StandardCopyOption.REPLACE_EXISTING);
            }
            deleteCurrentFileIfLocal(currentValue);
            return "/uploads/" + safeFolderName + "/" + storedFilename;
        } catch (IOException e) {
            throw new BusinessException("Không thể lưu file vào thư mục uploads: " + e.getMessage());
        }
    }

    private void deleteCurrentFileIfLocal(String currentValue) throws IOException {
        if (!StringUtils.hasText(currentValue) || !currentValue.startsWith("/uploads/")) {
            return;
        }

        String relativePath = currentValue.substring("/uploads/".length());
        if (!StringUtils.hasText(relativePath)) {
            return;
        }

        Path existingFile = uploadRoot.resolve(relativePath).normalize();
        validatePathInsideUploadRoot(existingFile);
        Files.deleteIfExists(existingFile);
    }

    private String sanitizeFolderName(String folderName) {
        String cleaned = StringUtils.hasText(folderName) ? folderName.trim() : "misc";
        cleaned = cleaned.replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
        cleaned = cleaned.replaceAll("[^a-zA-Z0-9/_-]", "-");
        if (!StringUtils.hasText(cleaned)) {
            return "misc";
        }
        return cleaned;
    }

    private String buildStoredFilename(String originalFilename) {
        String baseName = StringUtils.stripFilenameExtension(StringUtils.cleanPath(
                StringUtils.hasText(originalFilename) ? originalFilename : "upload"
        ));
        String extension = StringUtils.getFilenameExtension(originalFilename);

        String slug = baseName.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        if (!StringUtils.hasText(slug)) {
            slug = "upload";
        }

        String uniquePart = UUID.randomUUID().toString().replace("-", "");
        if (StringUtils.hasText(extension)) {
            return slug + "-" + uniquePart + "." + extension.toLowerCase();
        }
        return slug + "-" + uniquePart;
    }

    private void validatePathInsideUploadRoot(Path path) {
        if (!path.startsWith(uploadRoot)) {
            throw new BusinessException("Đường dẫn upload không hợp lệ.");
        }
    }
}
