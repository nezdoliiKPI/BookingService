package com.example.lab2.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

@Repository
public class PhotosRepository {
    private static final String STORAGE_DIR = "storage";
    private static final File storageDirectory = new File(STORAGE_DIR);

    PhotosRepository() {
        if (!storageDirectory.exists()) {
            storageDirectory.mkdirs();
        }
    }

    public void saveFile(String key, MultipartFile fileToSave) throws IOException {
        if (fileToSave == null) {
            throw new NullPointerException("fileToSave is null");
        }
        var targetFile = new File(STORAGE_DIR + File.separator + key);
        if (!Objects.equals(targetFile.getParent(), STORAGE_DIR)) {
            throw new SecurityException("Unsupported filename!");
        }
        Files.copy(fileToSave.getInputStream(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    public Resource getDownloadFile(String key) throws Exception {
        Path filePath = Paths.get(STORAGE_DIR).resolve(key);
        Resource resource = new UrlResource(filePath.toUri());
        return resource;
    }
}
