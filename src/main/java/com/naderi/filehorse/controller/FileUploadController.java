package com.naderi.filehorse.controller;

import com.naderi.filehorse.exception.StoreFileNotFoundException;
import com.naderi.sfu.service.FileStoreService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {
    private final FileStoreService fileStoreService;

    public FileUploadController(FileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }


    @GetMapping("/")
    public String listUploadedFiles(Model model) {
        model.addAttribute("files", fileStoreService.findAll().parallelStream().map(path -> path.getFileName().toString()).collect(Collectors.toList()));
        return "uploadForm";
    }

    @GetMapping("/files/file")
    @ResponseBody
    public ResponseEntity<Resource> loadFile(@RequestParam("filename") String filename) throws IOException {
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + filename + "\"").body(fileStoreService.readFileContent(filename));
    }

    @PostMapping("/")
    public String upload(@RequestParam("file") MultipartFile multipartFile,
                         RedirectAttributes redirectAttributes) throws IOException {
        String createdFileName = fileStoreService.save(multipartFile);
        redirectAttributes.addFlashAttribute("message", createdFileName == null ? String.format("Unable to store[%s] file or invalid file!", multipartFile.getOriginalFilename())
                : String.format("You successfully uploaded your file[%s]!", createdFileName));

        return "redirect:/";
    }

    @ExceptionHandler(StoreFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StoreFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
