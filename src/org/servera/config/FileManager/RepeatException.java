package org.servera.config.FileManager;

public class RepeatException extends RuntimeException
{
    public RepeatException(String message) {
        super(message);
    }

    public RepeatException()
    {
        super("Detected repeat keys.");
    }
}
