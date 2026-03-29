package com.streaming.movieplatform.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.streaming.movieplatform.exception.BusinessException;
import com.streaming.movieplatform.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService {

    @Value("${app.cloudinary.url:}")
    private String cloudinaryUrl;

    @Value("${app.cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${app.cloudinary.api-key:}")
    private String apiKey;

    @Value("${app.cloudinary.api-secret:}")
    private String apiSecret;

    @Value("${app.cloudinary.folder-prefix:movie-streaming-platform}")
    private String folderPrefix;

    private Cloudinary cloudinary;

    @PostConstruct
    void initCloudinary() {
        if (StringUtils.hasText(cloudinaryUrl)) {
            cloudinary = new Cloudinary(cloudinaryUrl.trim());
        } else if (StringUtils.hasText(cloudName) && StringUtils.hasText(apiKey) && StringUtils.hasText(apiSecret)) {
            cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName.trim(),
                    "api_key", apiKey.trim(),
                    "api_secret", apiSecret.trim()
            ));
        }

        if (cloudinary != null) {
            cloudinary.config.secure = true;
        }
    }

    @Override
    public String store(MultipartFile file, String folderName, String currentValue) {
        if (file == null || file.isEmpty()) {
            return currentValue;
        }
        if (cloudinary == null) {
            throw new BusinessException("Cloudinary chua duoc cau hinh. Hay them CLOUDINARY_URL hoac bo CLOUDINARY_CLOUD_NAME/CLOUDINARY_API_KEY/CLOUDINARY_API_SECRET.");
        }

        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "auto",
                            "folder", resolveFolder(folderName),
                            "public_id", buildPublicId(file.getOriginalFilename()),
                            "overwrite", false
                    )
            );

            Object secureUrl = uploadResult.get("secure_url");
            if (secureUrl == null || !StringUtils.hasText(secureUrl.toString())) {
                throw new BusinessException("Cloudinary khong tra ve secure_url hop le.");
            }
            return secureUrl.toString();
        } catch (IOException e) {
            throw new BusinessException("Khong the upload file len Cloudinary: " + e.getMessage());
        }
    }

    private String resolveFolder(String folderName) {
        String cleanedFolder = StringUtils.hasText(folderName)
                ? folderName.trim().replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "")
                : "misc";

        if (!StringUtils.hasText(folderPrefix)) {
            return cleanedFolder;
        }

        String cleanedPrefix = folderPrefix.trim().replace("\\", "/").replaceAll("^/+", "").replaceAll("/+$", "");
        return cleanedPrefix + "/" + cleanedFolder;
    }

    private String buildPublicId(String originalFilename) {
        String extensionlessName = StringUtils.hasText(originalFilename)
                ? StringUtils.stripFilenameExtension(StringUtils.cleanPath(originalFilename))
                : "upload";

        String slug = extensionlessName
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");

        if (!StringUtils.hasText(slug)) {
            slug = "upload";
        }

        return slug + "-" + UUID.randomUUID();
    }
}
