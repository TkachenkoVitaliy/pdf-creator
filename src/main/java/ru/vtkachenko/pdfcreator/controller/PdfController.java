package ru.vtkachenko.pdfcreator.controller;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vtkachenko.pdfcreator.service.ConvertService;
import ru.vtkachenko.pdfcreator.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/to-pdf")
public class PdfController {
    private final FileStorageService fileStorageService;
    private final ConvertService convertService;

    public PdfController(FileStorageService fileStorageService, ConvertService convertService) {
        this.fileStorageService = fileStorageService;
        this.convertService = convertService;
    }

    @PostMapping
    public ResponseEntity<Resource> convertToPdf(@RequestParam MultipartFile uploadedFile) {

        Path convertedFile = null;
        try {
            Path tempUploadedFile = fileStorageService.storeFile(uploadedFile);
            convertedFile = convertService.convertToPdf(tempUploadedFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(convertedFile));

            return ResponseEntity.ok().headers(headers).contentLength(convertedFile.toFile().length()).contentType(MediaType.APPLICATION_PDF).body(resource);
        } catch (IOException | InterruptedException e) {
            // TODO: сделать нормальную обработку ошибок
            throw new RuntimeException(e);
        } finally {
            if (convertedFile != null) {
                fileStorageService.deleteFileWithParentDirectory(convertedFile);
            }
        }
    }
}
