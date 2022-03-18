package com.naderi.sfu.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface FileStoreService {

    String save(MultipartFile multipartFile) throws IOException;

    InputStreamResource readFileContent(String filename) throws IOException;

    List<Path> findAll();


    void deleteAll();

}