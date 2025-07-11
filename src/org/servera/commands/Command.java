package org.servera.commands;

import java.util.LinkedList;

public abstract class Command implements CommandInterface {
    private final String name;
    private LinkedList<String> arguments;
    protected String permission;

    public Command(String name, String permission)
    {
        this.permission = permission;
        this.name = name;
    }

    public abstract boolean run();

    @Override
    public void setArguments(LinkedList<String> arguments)
    {
        if(!(arguments == null)) {
            this.arguments = arguments;
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public LinkedList<String> getArguments() {
        return this.arguments;
    }

    @Override
    public String getPermission()
    {
        return this.permission;
    }
}
