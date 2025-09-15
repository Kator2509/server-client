package org.servera.pkg;

public class PackageException extends RuntimeException {
    public PackageException(String message) {
        super(message);
    }

    public PackageException()
    {
        super("Package error.");
    }
}
