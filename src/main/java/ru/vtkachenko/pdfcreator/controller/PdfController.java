package ru.vtkachenko.pdfcreator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.observation.ObservationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.vtkachenko.pdfcreator.exception.UnsupportedFileExtensionException;
import ru.vtkachenko.pdfcreator.service.ConvertService;
import ru.vtkachenko.pdfcreator.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
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
    public ResponseEntity<Object> convertToPdf(@RequestParam MultipartFile uploadedFile) {
        log.info("Start converting file to pdf. Filename - {}, Filesize - {}KB", uploadedFile.getOriginalFilename(), uploadedFile.getSize()/1_000);
        Path convertedFile = null;
        try {
            Path tempUploadedFile = fileStorageService.storeFile(uploadedFile);
            convertedFile = convertService.convertToPdf(tempUploadedFile);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(convertedFile));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(convertedFile.toFile().length())
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(resource);
        } catch (IOException | InterruptedException e) {
            log.error("IO or Interrupted Exception - {}", e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ExceptionBody("Unexpected error"),
                    new HttpHeaders(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (UnsupportedFileExtensionException e) {
            log.warn(e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ExceptionBody(e.getMessage()),
                    new HttpHeaders(),
                    HttpStatus.UNPROCESSABLE_ENTITY);
        } finally {
            if (convertedFile != null) {
                fileStorageService.deleteFileWithParentDirectory(convertedFile);
                log.info("Temp converted file has been deleted - {}", convertedFile);
            }
        }
    }
}
