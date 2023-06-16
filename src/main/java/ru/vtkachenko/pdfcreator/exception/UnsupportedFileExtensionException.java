package ru.vtkachenko.pdfcreator.exception;

public class UnsupportedFileExtensionException extends RuntimeException {

    public UnsupportedFileExtensionException(String message) {
        super(message);
    }
    public UnsupportedFileExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
