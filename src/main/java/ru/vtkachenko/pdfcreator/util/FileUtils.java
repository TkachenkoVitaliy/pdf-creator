package ru.vtkachenko.pdfcreator.util;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class FileUtils {
    private static final String DELIMITER = ".";
    public static String getFileExtension(String originalFileName) {
        if (originalFileName == null) {
            throw new IllegalArgumentException("Original fileName can't be null");
        }

        String[] fileNameParts = originalFileName.split(Pattern.quote(DELIMITER));

        if (fileNameParts.length < 2) {
            return "";
        }

        return fileNameParts[fileNameParts.length - 1].toLowerCase(Locale.ENGLISH);
    }

    public static String changeFileExtensionInName(String originalFileName, String newExtension) {
        if (originalFileName == null) {
            throw new IllegalArgumentException("Original fileName can't be null");
        }

        String[] fileNameParts = originalFileName.split(Pattern.quote(DELIMITER));

        if (fileNameParts.length < 2) {
            return String.join(
                    DELIMITER,
                    originalFileName.replaceAll(Pattern.quote(DELIMITER), ""),
                    newExtension);
        }

        fileNameParts[fileNameParts.length - 1] = newExtension;
        return String.join(DELIMITER, fileNameParts);
    }

}
