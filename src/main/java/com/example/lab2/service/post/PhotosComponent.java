package com.example.lab2.service.post;

import java.io.IOException;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.example.lab2.repository.PhotosRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PhotosComponent {
    private final PhotosRepository photosRepository;

    private String getKeyPartFromId(Long postId) {
        return String.valueOf(postId);
    }

    public Optional<String> savePostPhoto(Long postId, MultipartFile fileToSaves) {
        String key = getKeyPartFromId(postId) + fileToSaves.getOriginalFilename();

        try {
            photosRepository.saveFile(key, fileToSaves);
            return Optional.of(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(null);
    }

    public Optional<Resource> getPostPhoto(String key) {
        try {
            return Optional.ofNullable(photosRepository.getDownloadFile(key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(null);
    }


}
