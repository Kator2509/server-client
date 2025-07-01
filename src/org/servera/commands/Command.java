package org.servera.commands;

import java.util.LinkedList;

public abstract class Command {
    private final String name;
    private LinkedList<String> arguments;

    public Command(String name)
    {
        this.name = name;

    }

    public abstract void run();

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
}
