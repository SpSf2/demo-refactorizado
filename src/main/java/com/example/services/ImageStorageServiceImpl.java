package com.example.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    private final Path rootLocation;

    public ImageStorageServiceImpl(@Value("${app.uploads.dir}") String uploadsDir) {
        this.rootLocation = Paths.get(uploadsDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo crear la carpeta de subidas: " + rootLocation, e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = getExtension(originalFilename);

        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("Formato no permitido: " + extension);
        }

        String storedFilename = UUID.randomUUID() + extension;

        try {
            Path destination = rootLocation.resolve(storedFilename);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo", e);
        }

        return storedFilename;
    }

    @Override
    public void delete(String path) {
        if (path == null || path.isBlank()) {
            return;
        }
        try {
            Path fileToDelete = rootLocation.resolve(path).normalize();
            Files.deleteIfExists(fileToDelete);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo borrar el archivo: " + path, e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    private boolean isAllowedExtension(String extension) {
        return extension.equals(".png")
                || extension.equals(".jpg")
                || extension.equals(".jpeg")
                || extension.equals(".gif")
                || extension.equals(".webp");
    }
}