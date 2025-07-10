package org.servera.commands;

import java.util.LinkedList;

public abstract class Command {
    private final String name;
    private LinkedList<String> arguments;
    protected String permission;

    public Command(String name, String permission)
    {
        this.permission = permission;
        this.name = name;
    }

    public abstract boolean run();

    public void setArguments(LinkedList<String> arguments)
    {
        if(!(arguments == null)) {
            this.arguments = arguments;
        }
    }

    public String getName() {
        return this.name;
    }

    public LinkedList<String> getArguments() {
        return this.arguments;
    }

    public String getPermission()
    {
        return this.permission;
    }
}
