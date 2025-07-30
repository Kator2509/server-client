package org.servera.modul;

public abstract class ServerModule
{
    private final String name;

    public ServerModule(String name)
    {
        this.name = name;
    }

    public abstract void initialize();

    public String getName()
    {
        return this.name;
    }
}
