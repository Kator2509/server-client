package org.servera.inheritance;

import java.util.UUID;

public class User
{
    private UUID uuid;
    private String name;

    public User(String name, UUID uuid)
    {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }
}
