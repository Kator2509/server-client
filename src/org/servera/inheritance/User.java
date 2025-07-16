package org.servera.inheritance;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class User
{
    protected UUID uuid;
    private String firstName, secondName, tab;

    public User(UUID uuid, String tab, String firstName, @Nullable String secondName)
    {
        this.firstName = firstName;
        this.secondName = secondName;
        this.uuid = uuid;
        this.tab = tab;
    }

    public User(UUID uuid, String tab, String firstName)
    {
        this.firstName = firstName;
        this.uuid = uuid;
        this.tab = tab;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getTab() {
        return this.tab;
    }
}
