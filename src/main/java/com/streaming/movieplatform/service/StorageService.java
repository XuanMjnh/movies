package com.streaming.movieplatform.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String store(MultipartFile file, String folderName, String currentValue);
}
