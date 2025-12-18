package org.servera.inheritance.auth;

import org.servera.inheritance.User;

import java.net.Inet4Address;

public class Session
{
    private final User user;
    private final Inet4Address address;

    public Session(User user, Inet4Address address)
    {
        this.user = user;
        this.address = address;
    }

    public Inet4Address getAddress() {
        return address;
    }

    public User getUser() {
        return user;
    }
}
