package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Override
    public String store(MultipartFile file, String folderName, String currentValue) {
        if (file == null || file.isEmpty()) {
            return currentValue;
        }
        try {
            String original = StringUtils.cleanPath(file.getOriginalFilename());
            String extension = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                extension = original.substring(dot);
            }
            String fileName = UUID.randomUUID() + extension;
            Path folderPath = Paths.get(uploadDir, folderName).toAbsolutePath().normalize();
            Files.createDirectories(folderPath);
            Path target = folderPath.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + folderName + "/" + fileName;
        } catch (IOException e) {
            throw new BusinessException("Không thể lưu file upload: " + e.getMessage());
        }
    }
}
