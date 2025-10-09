// src/main/java/com/project/hotel/service/FileStorageService.java

package com.project.hotel.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final Cloudinary cloudinary;

    /**
     * Tải file lên Cloudinary và trả về URL an toàn (https).
     * @param file Đối tượng MultipartFile cần tải lên.
     * @return URL của file đã tải lên.
     */
    public String saveFile(MultipartFile file) {
        try {
            // Tải lên và nhận kết quả
            Map result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            // Trả về URL an toàn (secure_url)
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Can not upload file to Cloudinary", e);
        }
    }

    /**
     * Xóa file khỏi Cloudinary dựa trên URL.
     * @param imageUrl URL của ảnh cần xóa.
     */
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        try {
            // Cloudinary xóa file dựa trên public_id, không phải URL
            String publicId = getPublicIdFromUrl(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                System.out.println("File deleted: " + publicId);
            }
        } catch (IOException e) {
            System.err.println("Can not delete file from Cloudinary: " + e.getMessage());
        }
    }

    /**
     * Trích xuất public_id từ URL của Cloudinary.
     * Ví dụ: từ "https://res.cloudinary.com/.../image/upload/v12345/folder/image.jpg"
     * sẽ trích xuất ra "folder/image"
     * @param imageUrl URL đầy đủ của ảnh.
     * @return Public ID của ảnh.
     */
    private String getPublicIdFromUrl(String imageUrl) {
        // Pattern để tìm public_id (phần nằm giữa version và tên file)
        Pattern pattern = Pattern.compile("/v\\d+/(.+)");
        Matcher matcher = pattern.matcher(imageUrl);
        if (matcher.find()) {
            String path = matcher.group(1);
            // Loại bỏ phần đuôi file (ví dụ .jpg, .png)
            int lastDotIndex = path.lastIndexOf('.');
            if (lastDotIndex != -1) {
                return path.substring(0, lastDotIndex);
            }
            return path;
        }
        return null;
    }
}