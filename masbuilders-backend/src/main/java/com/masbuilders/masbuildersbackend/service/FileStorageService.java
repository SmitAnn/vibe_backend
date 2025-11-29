package com.masbuilders.masbuildersbackend.service;



import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadImage(MultipartFile file);

    String uploadVideo(MultipartFile file);
}


