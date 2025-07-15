package org.servera.config;

public class ConfigException extends Exception
{
    public ConfigException() {
        super("Configuration path is empty.");
    }

    public ConfigException(String message)
    {
        super(message);
    }
}
