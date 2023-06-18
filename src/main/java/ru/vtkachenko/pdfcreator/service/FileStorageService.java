package ru.vtkachenko.pdfcreator.service;

import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.vtkachenko.pdfcreator.exception.IllegalFileException;
import ru.vtkachenko.pdfcreator.exception.UnsupportedFileExtensionException;
import ru.vtkachenko.pdfcreator.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

@Service
public class FileStorageService {

    private static final List<String> ACCEPTABLE_EXTENSIONS = Arrays.asList("xls", "xlsx", "doc", "docx");
    public Path storeFile(MultipartFile uploadedFile) throws IOException, UnsupportedFileExtensionException {
        Path tempFolder = Path.of(System.getProperty("java.io.tmpdir"));
        String originalFilename = uploadedFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalFileException("Can't store file without original filename");
        }
        String fileExtension = FileUtils.getFileExtension(originalFilename);

        if (ACCEPTABLE_EXTENSIONS.contains(fileExtension)) {
            Path tempFile = tempFolder.resolve(uploadedFile.getOriginalFilename());
            Files.copy(uploadedFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            return tempFile;
        } else {
            throw new UnsupportedFileExtensionException(
                    String.format("Unsupported file extension - [%s]", fileExtension)
            );
        }
    }

    public void deleteFileWithParentDirectory(Path filePath) {
        FileSystemUtils.deleteRecursively(filePath.getParent().toFile());
    }
}
