package com.project.hotel.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FileStorageService {
    String UPLOAD_DIR = "uploads/";

    public String saveFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(path.getParent());
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "/" + UPLOAD_DIR + fileName; // path trả về để lưu DB
        } catch (IOException e) {
            throw new RuntimeException("Upload file thất bại!", e);
        }
    }
    public void deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isBlank()) return;
            String cleanPath = filePath.replaceFirst("^/+", "");
            Path path = Paths.get(cleanPath);

            if (!path.isAbsolute()) {
                path = Paths.get("").resolve(cleanPath);
            }

            Files.deleteIfExists(path);
            System.out.println("Deleted file: " + path);
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
    }
}
