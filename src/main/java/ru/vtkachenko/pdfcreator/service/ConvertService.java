package ru.vtkachenko.pdfcreator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vtkachenko.pdfcreator.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
public class ConvertService {

    public Path convertToPdf (Path inputFilePath) throws IOException, InterruptedException {
        final String PDF_EXTENSION = "pdf";
        Path tempDir = Files.createTempDirectory("result");
        File inputFile = inputFilePath.toFile();
        String inputFileName = inputFile.getName();

        ProcessBuilder processBuilder = new ProcessBuilder(
                "soffice",
                "--headless",
                "--convert-to", "pdf",
                inputFile.getAbsolutePath(),
                "--outdir", tempDir.toAbsolutePath().toString()
        );

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // TODO: заменить System.out.println на logger
        String line;
        while((line = reader.readLine()) != null) {
            log.info("< libreoffice stdout+stderr > {}", line);
        }
        process.waitFor();
        Path convertedFile = tempDir.resolve(FileUtils.changeFileExtensionInName(inputFileName, PDF_EXTENSION));
        log.info("LibreOffice converted - {}", convertedFile);

        boolean isTempFileDeleted = inputFile.delete();
        if (isTempFileDeleted) {
            log.info("Temp file has been deleted - {}", inputFile);
        } else {
            log.warn("Can't delete temp file - {}", inputFile);
        }
        return convertedFile;
    }
}