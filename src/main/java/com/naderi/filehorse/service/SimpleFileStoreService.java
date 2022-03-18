package com.naderi.filehorse.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SimpleFileStoreService implements com.naderi.sfu.service.FileStoreService {
    Logger log = LoggerFactory.getLogger(SimpleFileStoreService.class);
    private Path storageDir;

    public SimpleFileStoreService() {
    }

    @PostConstruct
    private void createStorageDirectory() {
        try {
            storageDir = Path.of(System.getProperty("user.home"), "/FileHorse");
            if (Files.notExists(storageDir)) {
                storageDir = Files.createDirectory(storageDir);
            }
        } catch (Exception ex) {
            log.error("Unable to create main directory to store files:{}. Message:{}", System.getProperty("user.home") + "/FileHorse/", ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public String save(MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path path = Files.createFile(Path.of(storageDir.toAbsolutePath().toString(), multipartFile.getOriginalFilename()));
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            return path.toFile().getAbsolutePath();
        } catch (IOException e) {
            log.error("Unable to store file:{}. Message:{}", multipartFile.getOriginalFilename(), e.getMessage());
        }
        return null;
    }

    @Override
    public InputStreamResource readFileContent(String filename) throws IOException {
       return new InputStreamResource(new ByteArrayInputStream(Files.readAllBytes(Path.of(storageDir.toString(),filename))));
    }

    @Override
    public List<Path> findAll() {
        try {
            return Files.list(storageDir).collect(Collectors.toList());
        } catch (IOException ex) {
            log.error("Unable to load list of stored files:{}. Message:{}", storageDir.toAbsolutePath(), ex.getMessage());
            ex.printStackTrace();
        }
        return Collections.emptyList();
    }

    @Override
    public void deleteAll() {
        findAll().parallelStream().forEach(path -> {
            try {
                Files.deleteIfExists(path);
            } catch (IOException ex) {
                log.error("Unable to delete the file in path:{}. Message:{}", path.toAbsolutePath(), ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}