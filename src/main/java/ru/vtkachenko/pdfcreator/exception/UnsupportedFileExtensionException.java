package ru.vtkachenko.pdfcreator.exception;

public class UnsupportedFileExtensionException extends Exception {

    public UnsupportedFileExtensionException(String message) {
        super(message);
    }
    public UnsupportedFileExtensionException(String message, Throwable cause) {
        super(message, cause);
    }
}
