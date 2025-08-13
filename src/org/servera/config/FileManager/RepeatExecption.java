package org.servera.config.FileManager;

public class RepeatExecption extends RuntimeException
{
    public RepeatExecption(String message) {
        super(message);
    }

    public RepeatExecption()
    {
        super("Detected repeat keys.");
    }
}
