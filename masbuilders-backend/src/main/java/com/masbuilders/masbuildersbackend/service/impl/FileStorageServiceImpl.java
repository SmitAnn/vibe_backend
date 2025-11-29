package com.masbuilders.masbuildersbackend.service.impl;


import com.masbuilders.masbuildersbackend.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private static final String IMAGE_DIR = "uploads/images/";
    private static final String VIDEO_DIR = "uploads/videos/";

    @Override
    public String uploadImage(MultipartFile file) {
        return saveFile(file, IMAGE_DIR);
    }

    @Override
    public String uploadVideo(MultipartFile file) {
        return saveFile(file, VIDEO_DIR);
    }

    private String saveFile(MultipartFile file, String directory) {
        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dir = new File(directory);
            if (!dir.exists()) dir.mkdirs();

            File dest = new File(directory + fileName);
            file.transferTo(dest);

            return fileName;

        } catch (Exception e) {
            throw new RuntimeException("File upload failed");
        }
    }
}

