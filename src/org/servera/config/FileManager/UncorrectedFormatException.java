package org.servera.config.FileManager;

public class UncorrectedFormatException extends Exception
{
    public UncorrectedFormatException(String message) {
        super(message);
    }

    public UncorrectedFormatException() {
        super("Uncorrected format. Supported format JSON.");
    }
}
