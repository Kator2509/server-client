package org.servera.inheritance;

public class UnknowUser extends RuntimeException {
    public UnknowUser(String message) {
        super(message);
    }

    public UnknowUser() {
        super("Unknow user trying to login.");
    }
}
