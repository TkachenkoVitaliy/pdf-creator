package ru.vtkachenko.pdfcreator.service;

import org.springframework.stereotype.Service;
import ru.vtkachenko.pdfcreator.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

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

        System.out.println(inputFile.getAbsolutePath() + "\n" + tempDir.toAbsolutePath().toString());

        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        // TODO: заменить System.out.println на logger
        String line;
        while((line = reader.readLine()) != null) {
            System.out.println("[libreoffice stdout+stderr] " + line);
        }
        process.waitFor();
        System.out.println("converted " + tempDir);

        inputFile.delete();
        return tempDir.resolve(FileUtils.changeFileExtensionInName(inputFileName, PDF_EXTENSION));
    }
}